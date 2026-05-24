package br.com.eventpass.api.controller;

import br.com.eventpass.application.dto.request.ConvidadoRequest;
import br.com.eventpass.application.service.ConvidadoService;
import br.com.eventpass.domain.entity.Convidado;
import br.com.eventpass.infrastructure.qrcode.QrCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eventos/{eventoId}/convidados")
@RequiredArgsConstructor
public class ConvidadoController {

    private final ConvidadoService convidadoService;
    private final QrCodeService qrCodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Convidado criar(Authentication auth,
                           @PathVariable Long eventoId,
                           @Valid @RequestBody ConvidadoRequest req) {
        return convidadoService.criar(clienteId(auth), eventoId, req);
    }

    @PostMapping("/importar")
    public ResponseEntity<Map<String, Object>> importar(
            Authentication auth,
            @PathVariable Long eventoId,
            @RequestParam("arquivo") MultipartFile arquivo) {

        var resultado = convidadoService.importarPlanilha(clienteId(auth), eventoId, arquivo);
        return ResponseEntity.ok(Map.of(
            "total",   resultado.total(),
            "sucesso", resultado.sucesso(),
            "falha",   resultado.falha()
        ));
    }

    @PostMapping("/enviar-convites")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void enviarConvites(Authentication auth,
                                @PathVariable Long eventoId,
                                @RequestBody(required = false) List<Long> convidadoIds) {
        convidadoService.enviarConvites(clienteId(auth), eventoId, convidadoIds);
    }

    @GetMapping
    public Page<Convidado> listar(Authentication auth,
                                   @PathVariable Long eventoId,
                                   @PageableDefault(size = 50) Pageable pageable) {
        return convidadoService.listar(clienteId(auth), eventoId, pageable);
    }

    @GetMapping("/{id}")
    public Convidado buscar(Authentication auth,
                             @PathVariable Long eventoId,
                             @PathVariable Long id) {
        return convidadoService.buscar(clienteId(auth), eventoId, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(Authentication auth,
                         @PathVariable Long eventoId,
                         @PathVariable Long id) {
        convidadoService.excluir(clienteId(auth), eventoId, id);
    }

    /** Retorna imagem PNG do QR Code diretamente */
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> qrCode(Authentication auth,
                                          @PathVariable Long eventoId,
                                          @PathVariable Long id) {
        Convidado convidado = convidadoService.buscar(clienteId(auth), eventoId, id);
        byte[] imagem = qrCodeService.gerarQrCode(convidado.getUuid());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"qrcode-" + convidado.getUuid() + ".png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }

    private Long clienteId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
