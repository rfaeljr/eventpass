package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.FinalidadeOtp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "codigos_otp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodigoOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente; // null antes do cadastro

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false)
    private String codigo; // hash bcrypt do código de 6 dígitos

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinalidadeOtp finalidade;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "usado_em")
    private LocalDateTime usadoEm;

    @Column(nullable = false)
    private Integer tentativas = 0;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // ── Helpers ──────────────────────────────────────────────────────────────
    public boolean estaValido() {
        return usadoEm == null && LocalDateTime.now().isBefore(expiraEm);
    }

    public boolean atingiuMaxTentativas(int max) {
        return tentativas >= max;
    }

    public void incrementarTentativas() {
        this.tentativas++;
    }

    public void marcarComoUsado() {
        this.usadoEm = LocalDateTime.now();
    }
}
