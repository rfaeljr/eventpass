package br.com.eventpass.application.service;

import br.com.eventpass.application.dto.request.DispositivoRequest;
import br.com.eventpass.application.dto.request.PareamentoRequest;
import br.com.eventpass.application.dto.response.PareamentoResponse;
import br.com.eventpass.domain.entity.Cliente;
import br.com.eventpass.domain.entity.Dispositivo;
import br.com.eventpass.domain.entity.DispositivoEvento;
import br.com.eventpass.domain.entity.Evento;
import br.com.eventpass.domain.enums.StatusDispositivo;
import br.com.eventpass.domain.repository.ClienteRepository;
import br.com.eventpass.domain.repository.DispositivoEventoRepository;
import br.com.eventpass.domain.repository.DispositivoRepository;
import br.com.eventpass.domain.repository.EventoRepository;
import br.com.eventpass.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final DispositivoEventoRepository dispositivoEventoRepository;
    private final ClienteRepository clienteRepository;
    private final EventoRepository eventoRepository;
    private final JwtService jwtService;

    // ── Passo 1: Admin cria dispositivo e recebe QR Code de pareamento ────────
    @Transactional
    public PareamentoResponse criarEGerarPareamento(Long clienteId, DispositivoRequest req) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        validarLimiteDispositivos(clienteId);

        // Gera token de pareamento (JWT uso único, 10 min)
        String tokenPareamento = jwtService.gerarTokenPareamento(0L, clienteId);


        Dispositivo dispositivo = Dispositivo.builder()
                .cliente(cliente)
                .descricao(req.descricao())
                .tokenPareamento(tokenPareamento.substring(0,255))
                .pareamentoExpiraEm(LocalDateTime.now().plusMinutes(10))
                .status(StatusDispositivo.AGUARDANDO)
                .build();

        dispositivo = dispositivoRepository.save(dispositivo);

        // Atualiza token com ID real do dispositivo
        String tokenFinal = jwtService.gerarTokenPareamento(dispositivo.getId(), clienteId).substring(0,256);
        dispositivo.setTokenPareamento(tokenFinal);
        dispositivoRepository.save(dispositivo);

        return new PareamentoResponse(
            dispositivo.getId(),
            dispositivo.getDescricao(),
            tokenFinal,
            dispositivo.getPareamentoExpiraEm()
        );
    }

    // ── Passo 2: App Android lê QR e chama este endpoint com o token ─────────
    @Transactional
    public String concluirPareamento(PareamentoRequest req) {
        if (!jwtService.tokenValido(req.tokenPareamento())) {
            throw new IllegalArgumentException("Token de pareamento inválido ou expirado.");
        }

        Dispositivo dispositivo = dispositivoRepository
                .findByTokenPareamento(req.tokenPareamento())
                .orElseThrow(() -> new IllegalArgumentException("Token não encontrado."));

        if (!dispositivo.pareamentoValido()) {
            throw new IllegalArgumentException("Token de pareamento expirado.");
        }

        Long clienteId = dispositivo.getCliente().getId();
        String tokenDispositivo = jwtService.gerarTokenDispositivo(dispositivo.getId(), clienteId);
        dispositivo.concluirPareamento(tokenDispositivo);
        dispositivoRepository.save(dispositivo);

        return tokenDispositivo; // retornado ao app Android para salvar localmente
    }

    // ── Associar dispositivo a um evento ─────────────────────────────────────
    @Transactional
    public void associarAoEvento(Long clienteId, Long dispositivoId, Long eventoId) {
        Dispositivo dispositivo = buscarDoCliente(clienteId, dispositivoId);
        Evento evento = eventoRepository.findByIdAndClienteId(eventoId, clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        if (!dispositivo.estaAtivo()) {
            throw new IllegalStateException("Dispositivo não está ativo.");
        }

        // Desativa associações anteriores do device
        dispositivoEventoRepository.desativarPorDispositivo(dispositivoId);

        DispositivoEvento assoc = dispositivoEventoRepository
                .findByDispositivoIdAndEventoId(dispositivoId, eventoId)
                .orElse(DispositivoEvento.builder()
                        .dispositivo(dispositivo)
                        .evento(evento)
                        .build());

        assoc.setAtivo(true);
        dispositivoEventoRepository.save(assoc);
    }

    // ── Revogar acesso ────────────────────────────────────────────────────────
    @Transactional
    public void revogar(Long clienteId, Long dispositivoId) {
        Dispositivo dispositivo = buscarDoCliente(clienteId, dispositivoId);
        dispositivo.setStatus(StatusDispositivo.REVOGADO);
        dispositivo.setTokenDispositivo(null);
        dispositivoEventoRepository.desativarPorDispositivo(dispositivoId);
        dispositivoRepository.save(dispositivo);
    }

    @Transactional(readOnly = true)
    public List<Dispositivo> listar(Long clienteId) {
        return dispositivoRepository.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public Dispositivo buscarDoCliente(Long clienteId, Long dispositivoId) {
        return dispositivoRepository.findByIdAndClienteId(dispositivoId, clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado."));
    }

    private void validarLimiteDispositivos(Long clienteId) {
        // Limite de dispositivos validado pela assinatura — simplificado aqui
        long ativos = dispositivoRepository.countByClienteIdAndStatus(clienteId, StatusDispositivo.ATIVO);
        if (ativos >= 10) { // substituir pelo limite real do plano
            throw new IllegalStateException("Limite de dispositivos do plano atingido.");
        }
    }
}
