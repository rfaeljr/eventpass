package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.StatusConvite;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "convidados")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Convidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(columnDefinition = "char(36)", nullable = false, unique = true, length = 36)
    private String uuid; // chave do QR Code

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 20)
    private String documento; // CPF ou RG

    @Column(length = 20)
    private String telefone;

    @Column(length = 150)
    private String email;

    @Column(name = "grupo_tag", length = 60)
    private String grupoTag; // Mesa, Setor, Turma

    @Column(name = "max_acompanhantes", nullable = false)
    private Integer maxAcompanhantes = 0;

    @Column(name = "url_qrcode", length = 500)
    private String urlQrcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_convite", nullable = false)
    private StatusConvite statusConvite = StatusConvite.PENDENTE;

    @Column(name = "convite_enviado_em")
    private LocalDateTime conviteEnviadoEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // ── Hooks ────────────────────────────────────────────────────────────────
    @PrePersist
    private void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    public boolean conviteJaEnviado() {
        return statusConvite != StatusConvite.PENDENTE;
    }

    public void marcarComoEnviado() {
        this.statusConvite = StatusConvite.ENVIADO;
        this.conviteEnviadoEm = LocalDateTime.now();
    }
}
