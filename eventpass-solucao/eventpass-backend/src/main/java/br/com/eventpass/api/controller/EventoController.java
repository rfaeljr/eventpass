package br.com.eventpass.api.controller;

import br.com.eventpass.application.dto.request.EventoRequest;
import br.com.eventpass.application.service.EventoService;
import br.com.eventpass.domain.entity.Evento;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Evento criar(Authentication auth, @Valid @RequestBody EventoRequest req) {
        return eventoService.criar(clienteId(auth), req);
    }

    @GetMapping
    public Page<Evento> listar(Authentication auth,
                                @PageableDefault(size = 20) Pageable pageable) {
        return eventoService.listar(clienteId(auth), pageable);
    }

    @GetMapping("/{id}")
    public Evento buscar(Authentication auth, @PathVariable Long id) {
        return eventoService.buscarDoCliente(clienteId(auth), id);
    }

    @PatchMapping("/{id}/publicar")
    public Evento publicar(Authentication auth, @PathVariable Long id) {
        return eventoService.publicar(clienteId(auth), id);
    }

    @PatchMapping("/{id}/iniciar")
    public Evento iniciar(Authentication auth, @PathVariable Long id) {
        return eventoService.iniciar(clienteId(auth), id);
    }

    @PatchMapping("/{id}/encerrar")
    public Evento encerrar(Authentication auth, @PathVariable Long id) {
        return eventoService.encerrar(clienteId(auth), id);
    }

    @PatchMapping("/{id}/cancelar")
    public Evento cancelar(Authentication auth, @PathVariable Long id) {
        return eventoService.cancelar(clienteId(auth), id);
    }

    private Long clienteId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
