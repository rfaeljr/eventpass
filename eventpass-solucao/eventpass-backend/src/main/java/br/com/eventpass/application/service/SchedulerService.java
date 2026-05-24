package br.com.eventpass.application.service;

import br.com.eventpass.domain.entity.Cliente;
import br.com.eventpass.domain.enums.StatusCliente;
import br.com.eventpass.domain.enums.StatusEvento;
import br.com.eventpass.domain.repository.ClienteRepository;
import br.com.eventpass.domain.repository.CodigoOtpRepository;
import br.com.eventpass.domain.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ClienteRepository clienteRepository;
    private final CodigoOtpRepository codigoOtpRepository;
    private final EventoRepository eventoRepository;
    private final NotificacaoService notificacaoService;

    /** Todo dia às 02:00 — suspende contas com trial expirado sem plano pago */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void suspenderTrialsExpirados() {
        List<Cliente> expirados = clienteRepository.findTrialsExpirados(LocalDateTime.now());
        for (Cliente c : expirados) {
            c.setStatus(StatusCliente.SUSPENSO);
            clienteRepository.save(c);
            notificacaoService.enviarOtpWhatsApp(c.getTelefoneWhatsapp(),
                "⚠️ Seu período de trial do EventPass expirou! " +
                "Acesse eventpass.com.br/planos para continuar usando.");
            log.info("Trial expirado — cliente suspenso: {}", c.getEmail());
        }
        log.info("SchedulerService: {} trials suspensos.", expirados.size());
    }

    /** A cada hora — atualiza status de eventos para EM_ANDAMENTO ou ENCERRADO */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void atualizarStatusEventos() {
        LocalDateTime agora = LocalDateTime.now();

        // Publicados que já começaram → EM_ANDAMENTO
        eventoRepository.findAll().stream()
            .filter(e -> e.getStatus() == StatusEvento.PUBLICADO
                      && e.getIniciaEm().isBefore(agora))
            .forEach(e -> {
                e.setStatus(StatusEvento.EM_ANDAMENTO);
                eventoRepository.save(e);
                log.debug("Evento '{}' iniciado automaticamente.", e.getNome());
            });

        // Em andamento que já terminaram → ENCERRADO
        eventoRepository.findAll().stream()
            .filter(e -> e.getStatus() == StatusEvento.EM_ANDAMENTO
                      && e.getTerminaEm().isBefore(agora))
            .forEach(e -> {
                e.setStatus(StatusEvento.ENCERRADO);
                eventoRepository.save(e);
                log.debug("Evento '{}' encerrado automaticamente.", e.getNome());
            });
    }

    /** A cada 30 minutos — limpa OTPs expirados */
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void limparOtpsExpirados() {
        codigoOtpRepository.deletarExpirados(LocalDateTime.now());
        log.debug("SchedulerService: OTPs expirados removidos.");
    }
}
