package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.TipoCobranca;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "planos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String slug;

    @Column(nullable = false, length = 60)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cobranca", nullable = false)
    private TipoCobranca tipoCobranca;

    @Column(name = "preco_centavos", nullable = false)
    private Integer precoCentavos;

    @Column(name = "max_eventos")
    private Integer maxEventos; // null = ilimitado

    @Column(name = "max_convidados_por_evento")
    private Integer maxConvidadosPorEvento;

    @Column(name = "max_dispositivos", nullable = false)
    private Integer maxDispositivos;

    @Column(name = "tem_relatorios", nullable = false)
    private Boolean temRelatorios = false;

    @Column(nullable = false)
    private Boolean ativo = true;
}
