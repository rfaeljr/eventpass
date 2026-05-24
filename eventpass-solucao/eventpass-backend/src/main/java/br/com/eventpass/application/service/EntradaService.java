package br.com.eventpass.application.service;

import br.com.eventpass.application.dto.request.LeituraQrCodeRequest;
import br.com.eventpass.application.dto.response.ResultadoLeituraResponse;
import br.com.eventpass.domain.entity.*;
import br.com.eventpass.domain.enums.StatusEntrada;
import br.com.eventpass.domain.enums.StatusEvento;
import br.com.eventpass.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntradaService {

    private final EntradaRepository entradaRepository;
    private final ConvidadoRepository convidadoRepository;
    private final DispositivoRepository dispositivoRepository;
    private final DispositivoEventoRepository dispositivoEventoRepository;
    private final SimpMessagingTemplate websocket;

    @Transactional
    public ResultadoLeituraResponse processarLeitura(Long dispositivoId, LeituraQrCodeRequest req) {
        Dispositivo dispositivo = dispositivoRepository.findById(dispositivoId)
                .orElseThrow(() -> new IllegalStateException("Dispositivo não encontrado."));

        // 1. Busca o convidado pelo UUID do QR Code
        Convidado convidado = convidadoRepository.findByUuid(req.uuid()).orElse(null);
        if (convidado == null) {
            return registrarNegacao(dispositivo, null, null, "qr_invalido",
                    "QR Code não encontrado no sistema.");
        }

        Evento evento = convidado.getEvento();

        // 2. Verifica se o dispositivo está autorizado para este evento
        boolean autorizado = dispositivoEventoRepository
                .findByDispositivoIdAndEventoId(dispositivoId, evento.getId())
                .map(DispositivoEvento::getAtivo)
                .orElse(false);

        if (!autorizado) {
            return registrarNegacao(dispositivo, convidado, evento, "device_nao_autorizado",
                    "Dispositivo não autorizado para este evento.");
        }

        // 3. Verifica status do evento
        if (evento.getStatus() != StatusEvento.EM_ANDAMENTO) {
            return registrarNegacao(dispositivo, convidado, evento, "evento_nao_ativo",
                    "Evento não está em andamento.");
        }

        // 4. Verifica capacidade
        long totalEntradas = entradaRepository.countEntradasLiberadas(evento.getId());
        if (evento.atingiuCapacidade(totalEntradas)) {
            return registrarNegacao(dispositivo, convidado, evento, "capacidade_maxima",
                    "Capacidade máxima do evento atingida.");
        }

        // 5. Verifica se já entrou
        boolean jaEntrou = entradaRepository
                .existsByConvidadoIdAndStatus(convidado.getId(), StatusEntrada.LIBERADO);

        if (jaEntrou && !evento.getPermiteReentrada()) {
            return registrarReentradaNegada(dispositivo, convidado, evento);
        }

        // 6. Libera a entrada
        int qtdAcompanhantes = req.qtdAcompanhantes() != null
                ? Math.min(req.qtdAcompanhantes(), convidado.getMaxAcompanhantes())
                : 0;

        StatusEntrada statusEntrada = jaEntrou ? StatusEntrada.REENTRADA : StatusEntrada.LIBERADO;

        Entrada entrada = Entrada.builder()
                .convidado(convidado)
                .evento(evento)
                .dispositivo(dispositivo)
                .qtdAcompanhantes(qtdAcompanhantes)
                .status(statusEntrada)
                .build();

        entradaRepository.save(entrada);

        // 7. Notifica dashboard ao vivo via WebSocket
        notificarDashboard(evento.getId(), convidado, statusEntrada, qtdAcompanhantes);

        return ResultadoLeituraResponse.liberado(
                convidado.getNome(),
                convidado.getGrupoTag(),
                convidado.getMaxAcompanhantes(),
                qtdAcompanhantes,
                statusEntrada
        );
    }

    // ── Busca manual por nome (porteiro digita quando QR falha) ──────────────
    @Transactional(readOnly = true)
    public java.util.List<Convidado> buscarPorNome(Long dispositivoId, Long eventoId, String nome) {
        // Verifica autorização do device
        boolean autorizado = dispositivoEventoRepository
                .findByDispositivoIdAndEventoId(dispositivoId, eventoId)
                .map(DispositivoEvento::getAtivo)
                .orElse(false);

        if (!autorizado) throw new IllegalStateException("Dispositivo não autorizado.");

        return convidadoRepository.buscarPorNome(eventoId, nome);
    }

    // ── Relatório ao vivo ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public java.util.List<Entrada> listarEntradasEvento(Long eventoId) {
        return entradaRepository.findByEventoIdOrderByRegistradoEmDesc(eventoId);
    }

    // ── Helpers privados ──────────────────────────────────────────────────────
    private ResultadoLeituraResponse registrarNegacao(Dispositivo dispositivo,
                                                       Convidado convidado,
                                                       Evento evento,
                                                       String motivo,
                                                       String mensagem) {
        if (convidado != null && evento != null) {
            Entrada entrada = Entrada.builder()
                    .convidado(convidado)
                    .evento(evento)
                    .dispositivo(dispositivo)
                    .qtdAcompanhantes(0)
                    .status(StatusEntrada.NEGADO)
                    .motivoNegacao(motivo)
                    .build();
            entradaRepository.save(entrada);
        }
        log.warn("Entrada negada — motivo: {} — convidado: {}",
                 motivo, convidado != null ? convidado.getNome() : "desconhecido");
        return ResultadoLeituraResponse.negado(mensagem);
    }

    private ResultadoLeituraResponse registrarReentradaNegada(Dispositivo dispositivo,
                                                               Convidado convidado,
                                                               Evento evento) {
        return registrarNegacao(dispositivo, convidado, evento,
                "reentrada_nao_permitida", "Convidado já registrou entrada e reentrada não é permitida.");
    }

    private void notificarDashboard(Long eventoId, Convidado convidado,
                                     StatusEntrada status, int acompanhantes) {
        try {
            var msg = new java.util.HashMap<String, Object>();
            msg.put("convidadoNome", convidado.getNome());
            msg.put("grupoTag", convidado.getGrupoTag());
            msg.put("status", status.name());
            msg.put("acompanhantes", acompanhantes);
            msg.put("horario", java.time.LocalDateTime.now().toString());
            websocket.convertAndSend("/topic/evento/" + eventoId + "/entradas", msg);
        } catch (Exception e) {
            log.warn("Falha ao notificar WebSocket: {}", e.getMessage());
        }
    }
}
