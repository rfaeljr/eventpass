package br.com.eventpass.application.service;

import br.com.eventpass.application.dto.request.EventoRequest;
import br.com.eventpass.domain.entity.Assinatura;
import br.com.eventpass.domain.entity.Cliente;
import br.com.eventpass.domain.entity.Evento;
import br.com.eventpass.domain.enums.StatusEvento;
import br.com.eventpass.domain.repository.AssinaturaRepository;
import br.com.eventpass.domain.repository.ClienteRepository;
import br.com.eventpass.domain.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ClienteRepository clienteRepository;
    private final AssinaturaRepository assinaturaRepository;

    @Transactional
    public Evento criar(Long clienteId, EventoRequest req) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        validarLimiteEventos(clienteId);

        Evento evento = Evento.builder()
                .cliente(cliente)
                .nome(req.nome())
                .descricao(req.descricao())
                .local(req.local())
                .iniciaEm(req.iniciaEm())
                .terminaEm(req.terminaEm())
                .capacidadeMaxima(req.capacidadeMaxima())
                .maxAcompanhantes(req.maxAcompanhantes() != null ? req.maxAcompanhantes() : 0)
                .permiteReentrada(req.permiteReentrada() != null ? req.permiteReentrada() : false)
                .status(StatusEvento.RASCUNHO)
                .build();

        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento publicar(Long clienteId, Long eventoId) {
        Evento evento = buscarDoCliente(clienteId, eventoId);
        if (evento.getStatus() != StatusEvento.RASCUNHO) {
            throw new IllegalStateException("Somente rascunhos podem ser publicados.");
        }
        evento.setStatus(StatusEvento.PUBLICADO);
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento iniciar(Long clienteId, Long eventoId) {
        Evento evento = buscarDoCliente(clienteId, eventoId);
        if (evento.getStatus() != StatusEvento.PUBLICADO) {
            throw new IllegalStateException("Somente eventos publicados podem ser iniciados.");
        }
        evento.setStatus(StatusEvento.EM_ANDAMENTO);
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento encerrar(Long clienteId, Long eventoId) {
        Evento evento = buscarDoCliente(clienteId, eventoId);
        evento.setStatus(StatusEvento.ENCERRADO);
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento cancelar(Long clienteId, Long eventoId) {
        Evento evento = buscarDoCliente(clienteId, eventoId);
        evento.setStatus(StatusEvento.CANCELADO);
        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public Page<Evento> listar(Long clienteId, Pageable pageable) {
        return eventoRepository.findByClienteIdOrderByCriadoEmDesc(clienteId, pageable);
    }

    @Transactional(readOnly = true)
    public Evento buscarDoCliente(Long clienteId, Long eventoId) {
        return eventoRepository.findByIdAndClienteId(eventoId, clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
    }

    @Transactional(readOnly = true)
    public Evento buscarPorUuid(String uuid) {
        return eventoRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
    }

    private void validarLimiteEventos(Long clienteId) {
        Assinatura assinatura = assinaturaRepository
                .findTopByClienteIdOrderByCriadoEmDesc(clienteId)
                .orElseThrow(() -> new IllegalStateException("Nenhum plano ativo encontrado."));

        Integer maxEventos = assinatura.getPlano().getMaxEventos();
        if (maxEventos != null) {
            long totalEventos = eventoRepository.countByClienteId(clienteId);
            if (totalEventos >= maxEventos) {
                throw new IllegalStateException(
                    "Limite de eventos do plano atingido (%d/%d). Faça upgrade para continuar."
                    .formatted(totalEventos, maxEventos));
            }
        }
    }
}
