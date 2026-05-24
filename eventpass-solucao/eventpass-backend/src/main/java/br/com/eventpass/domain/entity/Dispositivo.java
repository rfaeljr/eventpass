package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.StatusDispositivo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    @Column(name = "token_dispositivo", unique = true, length = 256)
    private String tokenDispositivo; // gerado após o pareamento concluído

    @Column(name = "token_pareamento", unique = true, length = 256)
    private String tokenPareamento;  // JWT de uso único, expira em 10min

    @Column(name = "pareamento_expira_em")
    private LocalDateTime pareamentoExpiraEm;

    @Column(nullable = false, length = 80)
    private String descricao; // Ex: "Portaria Principal"

    @Column(name = "modelo_android", length = 100)
    private String modeloAndroid;

    @Column(name = "versao_app", length = 20)
    private String versaoApp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDispositivo status = StatusDispositivo.AGUARDANDO;

    @Column(name = "ultimo_acesso_em")
    private LocalDateTime ultimoAcessoEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // ── Helpers ──────────────────────────────────────────────────────────────
    public boolean pareamentoValido() {
        return tokenPareamento != null
                && pareamentoExpiraEm != null
                && LocalDateTime.now().isBefore(pareamentoExpiraEm);
    }

    public boolean estaAtivo() {
        return status == StatusDispositivo.ATIVO;
    }

    public void concluirPareamento(String tokenDispositivo) {
        this.tokenDispositivo = tokenDispositivo;
        this.tokenPareamento = null;    // invalida token de uso único
        this.pareamentoExpiraEm = null;
        this.status = StatusDispositivo.ATIVO;
    }

    public void registrarAcesso(String modeloAndroid, String versaoApp) {
        this.ultimoAcessoEm = LocalDateTime.now();
        if (modeloAndroid != null) this.modeloAndroid = modeloAndroid;
        if (versaoApp != null)     this.versaoApp = versaoApp;
    }
}
