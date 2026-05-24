package br.com.eventpass.api.controller;

import br.com.eventpass.application.service.EntradaService;
import br.com.eventpass.application.service.EventoService;
import br.com.eventpass.domain.entity.Entrada;
import br.com.eventpass.domain.entity.Evento;
import br.com.eventpass.domain.repository.ConvidadoRepository;
import br.com.eventpass.domain.repository.EntradaRepository;
import br.com.eventpass.domain.enums.StatusEntrada;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eventos/{eventoId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EventoService eventoService;
    private final EntradaService entradaService;
    private final EntradaRepository entradaRepository;
    private final ConvidadoRepository convidadoRepository;

    @GetMapping
    public Map<String, Object> resumo(Authentication auth, @PathVariable Long eventoId) {
        Evento evento = eventoService.buscarDoCliente(clienteId(auth), eventoId);

        long totalConvidados  = convidadoRepository.countByEventoId(eventoId);
        long totalEntradas    = entradaRepository.countEntradasLiberadas(eventoId);
        Long totalAcomp       = entradaRepository.somarAcompanhantes(eventoId);
        long totalNegadas     = entradaRepository
                .findByEventoIdOrderByRegistradoEmDesc(eventoId).stream()
                .filter(e -> e.getStatus() == StatusEntrada.NEGADO).count();

        Integer capacidade = evento.getCapacidadeMaxima();
        double percentual  = (capacidade != null && capacidade > 0)
                ? (totalEntradas * 100.0 / capacidade) : 0;

        return Map.of(
            "eventoNome",        evento.getNome(),
            "eventoStatus",      evento.getStatus().name(),
            "totalConvidados",   totalConvidados,
            "totalEntradas",     totalEntradas,
            "totalAcompanhantes",totalAcomp != null ? totalAcomp : 0,
            "totalNegadas",      totalNegadas,
            "capacidadeMaxima",  capacidade != null ? capacidade : "ilimitado",
            "percentualOcupacao",String.format("%.1f", percentual)
        );
    }

    @GetMapping("/entradas")
    public List<Entrada> entradas(Authentication auth, @PathVariable Long eventoId) {
        eventoService.buscarDoCliente(clienteId(auth), eventoId);
        return entradaService.listarEntradasEvento(eventoId);
    }

    private Long clienteId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
