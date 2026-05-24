package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.StatusCliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telefone_whatsapp", nullable = false, length = 20)
    private String telefoneWhatsapp;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "telefone_verificado", nullable = false)
    private Boolean telefoneVerificado = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCliente status = StatusCliente.ATIVO;

    @Column(name = "trial_expira_em")
    private LocalDateTime trialExpiraEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    // ── Relacionamentos ──────────────────────────────────────────────────────
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Evento> eventos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Dispositivo> dispositivos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Assinatura> assinaturas = new ArrayList<>();

    // ── Helpers ──────────────────────────────────────────────────────────────
    public boolean estaEmTrial() {
        return trialExpiraEm != null && LocalDateTime.now().isBefore(trialExpiraEm);
    }

    public boolean trialExpirado() {
        return trialExpiraEm != null && LocalDateTime.now().isAfter(trialExpiraEm);
    }

    public boolean estaAtivo() {
        return status == StatusCliente.ATIVO;
    }
}
