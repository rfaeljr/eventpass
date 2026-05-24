package br.com.eventpass.domain.entity;

import br.com.eventpass.domain.enums.MetodoPagamento;
import br.com.eventpass.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assinatura_id")
    private Assinatura assinatura;

    @Column(name = "valor_centavos", nullable = false)
    private Integer valorCentavos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(name = "mp_pagamento_id", unique = true, length = 100)
    private String mpPagamentoId;

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}
