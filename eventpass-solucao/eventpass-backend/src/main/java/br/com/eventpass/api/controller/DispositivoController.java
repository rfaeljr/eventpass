package br.com.eventpass.api.controller;

import br.com.eventpass.application.dto.request.DispositivoRequest;
import br.com.eventpass.application.dto.request.PareamentoRequest;
import br.com.eventpass.application.dto.response.PareamentoResponse;
import br.com.eventpass.application.service.DispositivoService;
import br.com.eventpass.domain.entity.Dispositivo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dispositivos")
@RequiredArgsConstructor
public class DispositivoController {

    private final DispositivoService dispositivoService;

    /** Painel Admin: cria device e recebe token de pareamento (QR Code) */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PareamentoResponse criar(Authentication auth,
                                    @Valid @RequestBody DispositivoRequest req) {
        return dispositivoService.criarEGerarPareamento(clienteId(auth), req);
    }

    /** App Android: conclui o pareamento trocando o token temporário pelo permanente */
    @PostMapping("/pareamento/concluir")
    public ResponseEntity<Map<String, String>> concluirPareamento(
            @Valid @RequestBody PareamentoRequest req) {
        String tokenDispositivo = dispositivoService.concluirPareamento(req);
        return ResponseEntity.ok(Map.of("tokenDispositivo", tokenDispositivo));
    }

    /** Painel Admin: associa device a um evento */
    @PutMapping("/{dispositivoId}/evento/{eventoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void associarEvento(Authentication auth,
                                @PathVariable Long dispositivoId,
                                @PathVariable Long eventoId) {
        dispositivoService.associarAoEvento(clienteId(auth), dispositivoId, eventoId);
    }

    @GetMapping
    public List<Dispositivo> listar(Authentication auth) {
        return dispositivoService.listar(clienteId(auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revogar(Authentication auth, @PathVariable Long id) {
        dispositivoService.revogar(clienteId(auth), id);
    }

    private Long clienteId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
