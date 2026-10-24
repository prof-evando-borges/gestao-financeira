package br.com.fiap.gestaofinanceira.dividas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TB_DIV_PARCELA")
@Getter
@Setter
@NoArgsConstructor
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_DIVIDA", nullable = false)
    private Divida divida;

    // Tabela de OUTRO modulo (Contas): guarda so o id, sem @ManyToOne
    @Positive
    @Column(name = "ID_CONTA")
    private Long idConta;

    @NotNull
    @Positive
    @Column(name = "NUMERO", nullable = false)
    private Integer numero;

    @NotNull
    @Positive
    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull
    @Column(name = "DATA_VENCIMENTO", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "DATA_PAGAMENTO")
    private LocalDate dataPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusParcela status = StatusParcela.PENDENTE;
}
