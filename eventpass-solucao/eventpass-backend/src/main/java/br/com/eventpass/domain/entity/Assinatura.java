package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.StatusAssinatura;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "assinaturas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plano_id", nullable = false)
    private Plano plano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAssinatura status = StatusAssinatura.TRIAL;

    @Column(name = "inicia_em", nullable = false)
    private LocalDateTime iniciaEm;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    @Column(name = "renova_em")
    private LocalDateTime renovaEm;

    @Column(name = "mp_assinatura_id", length = 100)
    private String mpAssinaturaId;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // ── Helpers ──────────────────────────────────────────────────────────────
    public boolean estaAtiva() {
        return status == StatusAssinatura.ATIVO || status == StatusAssinatura.TRIAL;
    }

    public boolean estaExpirada() {
        return expiraEm != null && LocalDateTime.now().isAfter(expiraEm);
    }
}
