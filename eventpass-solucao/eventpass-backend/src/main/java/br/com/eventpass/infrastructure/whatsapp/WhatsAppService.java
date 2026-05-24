package br.com.eventpass.infrastructure.whatsapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class WhatsAppService {

    private final WebClient webClient;

    @Value("${whatsapp.instance-id:}")
    private String instanceId;

    @Value("${whatsapp.token:}")
    private String token;

    @Value("${whatsapp.client-token:}")
    private String clientToken;

    public WhatsAppService(@Value("${whatsapp.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public void enviarTexto(String telefone, String mensagem) {
        if (instanceId.isBlank()) {
            log.warn("[WhatsApp MOCK] Para: {} | Mensagem: {}", telefone, mensagem);
            return;
        }
        try {
            webClient.post()
                .uri("/instances/{id}/token/{token}/send-text", instanceId, token)
                .header("Client-Token", clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("phone", formatarTelefone(telefone), "message", mensagem))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            log.info("WhatsApp enviado para {}", telefone);
        } catch (Exception e) {
            log.error("Falha ao enviar WhatsApp para {}: {}", telefone, e.getMessage());
        }
    }

    public void enviarOtp(String telefone, String codigo) {
        String msg = "🔐 *EventPass* — Seu código de verificação é: *" + codigo + "*\n" +
                     "_Válido por 5 minutos. Não compartilhe com ninguém._";
        enviarTexto(telefone, msg);
    }

    public void enviarConvite(String telefone, String nomeConvidado, String nomeEvento,
                               String data, String local, String urlConvite) {
        String msg = "🎉 Olá, *" + nomeConvidado + "*!\n\n" +
                     "Você foi convidado(a) para *" + nomeEvento + "*\n" +
                     "📅 " + data + "\n" +
                     "📍 " + local + "\n\n" +
                     "Acesse seu convite com QR Code:\n" + urlConvite;
        enviarTexto(telefone, msg);
    }

    private String formatarTelefone(String telefone) {
        return telefone.replaceAll("[^0-9]", "");
    }
}
