package br.com.eventpass.application.service;

import br.com.eventpass.infrastructure.whatsapp.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final WhatsAppService whatsAppService;

    @Async
    public void enviarOtpWhatsApp(String telefone, String codigo) {
        whatsAppService.enviarOtp(telefone, codigo);
    }

    @Async
    public void enviarConviteWhatsApp(String telefone, String nomeConvidado,
                                       String nomeEvento, String data,
                                       String local, String urlConvite) {
        whatsAppService.enviarConvite(telefone, nomeConvidado, nomeEvento, data, local, urlConvite);
    }
}
