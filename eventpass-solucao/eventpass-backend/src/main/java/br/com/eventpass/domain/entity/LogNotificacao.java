package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.CanalNotificacao;
import br.com.eventpass.domain.enums.StatusNotificacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs_notificacoes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LogNotificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convidado_id")
    private Convidado convidado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalNotificacao canal;

    @Column(nullable = false, length = 150)
    private String destinatario;

    @Column(nullable = false, length = 60)
    private String template;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusNotificacao status = StatusNotificacao.NA_FILA;

    @Column(name = "ref_provedor", length = 100)
    private String refProvedor;

    @Column(name = "msg_erro", columnDefinition = "TEXT")
    private String msgErro;

    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}
