package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.StatusEntrada;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "entradas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convidado_id", nullable = false)
    private Convidado convidado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @Column(name = "qtd_acompanhantes", nullable = false)
    private Integer qtdAcompanhantes = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEntrada status;

    @Column(name = "motivo_negacao", length = 100)
    private String motivoNegacao;

    @CreationTimestamp
    @Column(name = "registrado_em", nullable = false, updatable = false)
    private LocalDateTime registradoEm;
}
