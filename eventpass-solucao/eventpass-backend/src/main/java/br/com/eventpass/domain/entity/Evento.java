package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.StatusEvento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "eventos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(columnDefinition = "char(36)", nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 255)
    private String local;

    @Column(name = "url_banner", length = 500)
    private String urlBanner;

    @Column(name = "inicia_em", nullable = false)
    private LocalDateTime iniciaEm;

    @Column(name = "termina_em", nullable = false)
    private LocalDateTime terminaEm;

    @Column(name = "capacidade_maxima")
    private Integer capacidadeMaxima;

    @Column(name = "max_acompanhantes", nullable = false)
    private Integer maxAcompanhantes = 0;

    @Column(name = "permite_reentrada", nullable = false)
    private Boolean permiteReentrada = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status = StatusEvento.RASCUNHO;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    // ── Relacionamentos ──────────────────────────────────────────────────────
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Convidado> convidados = new ArrayList<>();

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<DispositivoEvento> dispositivoEventos = new ArrayList<>();

    // ── Hooks ────────────────────────────────────────────────────────────────
    @PrePersist
    private void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    public boolean estaAtivo() {
        return status == StatusEvento.PUBLICADO || status == StatusEvento.EM_ANDAMENTO;
    }

    public boolean aceitaNovasEntradas() {
        return status == StatusEvento.EM_ANDAMENTO;
    }

    public boolean atingiuCapacidade(long totalEntradas) {
        return capacidadeMaxima != null && totalEntradas >= capacidadeMaxima;
    }
}
