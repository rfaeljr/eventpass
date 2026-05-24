package br.com.eventpass.application.service;

import br.com.eventpass.application.dto.request.ConvidadoRequest;
import br.com.eventpass.domain.entity.Convidado;
import br.com.eventpass.domain.entity.Evento;
import br.com.eventpass.domain.repository.ConvidadoRepository;
import br.com.eventpass.infrastructure.planilha.PlanilhaService;
import br.com.eventpass.infrastructure.qrcode.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConvidadoService {

    private final ConvidadoRepository convidadoRepository;
    private final EventoService eventoService;
    private final QrCodeService qrCodeService;
    private final PlanilhaService planilhaService;
    private final NotificacaoService notificacaoService;

    // ── Cadastro manual ───────────────────────────────────────────────────────
    @Transactional
    public Convidado criar(Long clienteId, Long eventoId, ConvidadoRequest req) {
        Evento evento = eventoService.buscarDoCliente(clienteId, eventoId);

        Convidado convidado = Convidado.builder()
                .evento(evento)
                .nome(req.nome())
                .documento(req.documento())
                .telefone(req.telefone())
                .email(req.email())
                .grupoTag(req.grupoTag())
                .maxAcompanhantes(req.maxAcompanhantes() != null ? req.maxAcompanhantes() : 0)
                .build();

        convidado = convidadoRepository.save(convidado);
        gerarEAssociarQrCode(convidado);
        return convidadoRepository.save(convidado);
    }

    // ── Importação por planilha ───────────────────────────────────────────────
    @Transactional
    public ImportacaoResultado importarPlanilha(Long clienteId, Long eventoId, MultipartFile arquivo) {
        Evento evento = eventoService.buscarDoCliente(clienteId, eventoId);
        List<ConvidadoRequest> linhas = planilhaService.parsear(arquivo);

        int sucesso = 0, falha = 0;
        for (ConvidadoRequest req : linhas) {
            try {
                Convidado convidado = Convidado.builder()
                        .evento(evento)
                        .nome(req.nome())
                        .documento(req.documento())
                        .telefone(req.telefone())
                        .email(req.email())
                        .grupoTag(req.grupoTag())
                        .maxAcompanhantes(req.maxAcompanhantes() != null ? req.maxAcompanhantes() : 0)
                        .build();
                convidado = convidadoRepository.save(convidado);
                gerarEAssociarQrCode(convidado);
                convidadoRepository.save(convidado);
                sucesso++;
            } catch (Exception e) {
                log.warn("Falha ao importar convidado '{}': {}", req.nome(), e.getMessage());
                falha++;
            }
        }
        return new ImportacaoResultado(linhas.size(), sucesso, falha);
    }

    // ── Envio de convites ─────────────────────────────────────────────────────
    @Transactional
    public void enviarConvites(Long clienteId, Long eventoId, List<Long> convidadoIds) {
        Evento evento = eventoService.buscarDoCliente(clienteId, eventoId);

        List<Convidado> lista = convidadoIds == null || convidadoIds.isEmpty()
                ? convidadoRepository.findByEventoIdAndStatusConvite(eventoId,
                        br.com.eventpass.domain.enums.StatusConvite.PENDENTE)
                : convidadoIds.stream()
                        .map(id -> convidadoRepository.findByIdAndEventoId(id, eventoId)
                                .orElseThrow())
                        .toList();

        for (Convidado c : lista) {
            if (c.getTelefone() != null && !c.getTelefone().isBlank()) {
                notificacaoService.enviarConviteWhatsApp(
                        c.getTelefone(),
                        c.getNome(),
                        evento.getNome(),
                        evento.getIniciaEm().toString(),
                        evento.getLocal() != null ? evento.getLocal() : "A confirmar",
                        "https://eventpass.com.br/convite/" + c.getUuid()
                );
            }
            c.marcarComoEnviado();
            convidadoRepository.save(c);
        }
    }

    // ── Listagem e busca ──────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<Convidado> listar(Long clienteId, Long eventoId, Pageable pageable) {
        eventoService.buscarDoCliente(clienteId, eventoId); // valida posse
        return convidadoRepository.findByEventoId(eventoId, pageable);
    }

    @Transactional(readOnly = true)
    public Convidado buscar(Long clienteId, Long eventoId, Long convidadoId) {
        eventoService.buscarDoCliente(clienteId, eventoId);
        return convidadoRepository.findByIdAndEventoId(convidadoId, eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Convidado não encontrado."));
    }

    @Transactional
    public void excluir(Long clienteId, Long eventoId, Long convidadoId) {
        Convidado convidado = buscar(clienteId, eventoId, convidadoId);
        convidadoRepository.delete(convidado);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void gerarEAssociarQrCode(Convidado convidado) {
        try {
            // O QR Code contém apenas o UUID do convidado
            byte[] qrBytes = qrCodeService.gerarQrCode(convidado.getUuid());
            // Em produção: fazer upload para S3 e salvar URL
            // Aqui simplificado — URL temporária local
            convidado.setUrlQrcode("/api/convidados/" + convidado.getId() + "/qrcode");
        } catch (Exception e) {
            log.error("Falha ao gerar QR Code para convidado {}: {}", convidado.getId(), e.getMessage());
        }
    }

    public record ImportacaoResultado(int total, int sucesso, int falha) {}
}
