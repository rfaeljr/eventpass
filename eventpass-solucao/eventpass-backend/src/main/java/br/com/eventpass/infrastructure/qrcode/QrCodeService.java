package br.com.eventpass.infrastructure.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Slf4j
@Service
public class QrCodeService {

    private static final int LARGURA  = 400;
    private static final int ALTURA   = 400;

    /**
     * Gera imagem PNG do QR Code a partir do UUID do convidado.
     * O UUID é o conteúdo gravado no QR — simples e seguro.
     */
    public byte[] gerarQrCode(String conteudo) {
        try {
            var hints = Map.of(
                EncodeHintType.CHARACTER_SET, "UTF-8",
                EncodeHintType.MARGIN, 1
            );
            var writer = new QRCodeWriter();
            var matrix = writer.encode(conteudo, BarcodeFormat.QR_CODE, LARGURA, ALTURA, hints);

            var out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Erro ao gerar QR Code para: {}", conteudo, e);
            throw new RuntimeException("Falha ao gerar QR Code.", e);
        }
    }
}
