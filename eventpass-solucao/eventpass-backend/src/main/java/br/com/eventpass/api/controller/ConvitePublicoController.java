package br.com.eventpass.api.controller;

import br.com.eventpass.domain.entity.Convidado;
import br.com.eventpass.domain.repository.ConvidadoRepository;
import br.com.eventpass.infrastructure.qrcode.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints públicos para o convidado acessar seu convite via link.
 * Não exige autenticação — identificado apenas pelo UUID do convidado.
 */
@RestController
@RequestMapping("/convites")
@RequiredArgsConstructor
public class ConvitePublicoController {

    private final ConvidadoRepository convidadoRepository;
    private final QrCodeService qrCodeService;

    /** Dados do convite para exibição na página pública */
    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> buscar(@PathVariable String uuid) {
        Convidado convidado = convidadoRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Convite não encontrado."));

        var evento = convidado.getEvento();
        return ResponseEntity.ok(Map.of(
            "convidadoNome",   convidado.getNome(),
            "eventoNome",      evento.getNome(),
            "eventoLocal",     evento.getLocal() != null ? evento.getLocal() : "",
            "eventoIniciaEm",  evento.getIniciaEm().toString(),
            "eventoStatus",    evento.getStatus().name(),
            "grupoTag",        convidado.getGrupoTag() != null ? convidado.getGrupoTag() : "",
            "maxAcompanhantes",convidado.getMaxAcompanhantes(),
            "statusConvite",   convidado.getStatusConvite().name(),
            "urlBannerEvento", evento.getUrlBanner() != null ? evento.getUrlBanner() : ""
        ));
    }

    /** QR Code em PNG diretamente — para exibir na tela do convidado */
    @GetMapping("/{uuid}/qrcode.png")
    public ResponseEntity<byte[]> qrCode(@PathVariable String uuid) {
        convidadoRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Convite não encontrado."));

        byte[] imagem = qrCodeService.gerarQrCode(uuid);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"convite.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }

    /** Convidado confirma presença */
    @PostMapping("/{uuid}/confirmar")
    public ResponseEntity<Map<String, String>> confirmar(@PathVariable String uuid) {
        Convidado convidado = convidadoRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Convite não encontrado."));

        convidado.setStatusConvite(br.com.eventpass.domain.enums.StatusConvite.CONFIRMADO);
        convidadoRepository.save(convidado);
        return ResponseEntity.ok(Map.of("mensagem", "Presença confirmada com sucesso!"));
    }
}
