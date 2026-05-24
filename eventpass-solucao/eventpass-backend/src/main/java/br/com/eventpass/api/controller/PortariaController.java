package br.com.eventpass.api.controller;

import br.com.eventpass.application.dto.request.LeituraQrCodeRequest;
import br.com.eventpass.application.dto.response.ResultadoLeituraResponse;
import br.com.eventpass.application.service.EntradaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portaria")
@RequiredArgsConstructor
public class PortariaController {

    private final EntradaService entradaService;

    /** App Android: lê QR Code e registra tentativa de entrada */
    @PostMapping("/leitura")
    public ResultadoLeituraResponse processarLeitura(
            Authentication auth,
            @Valid @RequestBody LeituraQrCodeRequest req) {
        Long dispositivoId = (Long) auth.getPrincipal();
        return entradaService.processarLeitura(dispositivoId, req);
    }

    /** App Android: busca convidado por nome (fallback manual) */
    @GetMapping("/buscar")
    public java.util.List<br.com.eventpass.domain.entity.Convidado> buscarPorNome(
            Authentication auth,
            @RequestParam Long eventoId,
            @RequestParam String nome) {
        Long dispositivoId = (Long) auth.getPrincipal();
        return entradaService.buscarPorNome(dispositivoId, eventoId, nome);
    }
}
