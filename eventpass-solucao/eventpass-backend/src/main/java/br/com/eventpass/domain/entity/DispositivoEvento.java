package br.com.eventpass.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "dispositivo_eventos",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_dispositivo_eventos",
        columnNames = {"dispositivo_id", "evento_id"}
    )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DispositivoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(nullable = false)
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "associado_em", nullable = false, updatable = false)
    private LocalDateTime associadoEm;
}
