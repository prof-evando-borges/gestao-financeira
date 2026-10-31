package br.com.fiap.gestaofinanceira.relatorios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Linha consolidada de um relatorio: quanto uma categoria somou no periodo
 * e quanto isso representa do total.
 */
@Entity
@Table(name = "TB_REL_ITEM")
@Getter
@Setter
@NoArgsConstructor
public class ItemRelatorio {

    private static final BigDecimal CEM = new BigDecimal("100");

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36, nullable = false)
    private String id;

    @NotBlank
    @Size(max = 36)
    @Column(name = "ID_RELATORIO", length = 36, nullable = false)
    private String idRelatorio;

    @NotBlank
    @Size(max = 50)
    @Column(name = "CATEGORIA", length = 50, nullable = false)
    private String categoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_OPERACAO", length = 20, nullable = false)
    private TipoOperacao tipoOperacao;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 13, fraction = 2)
    @Column(name = "VALOR_TOTAL", precision = 15, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "PERCENTUAL", precision = 5, scale = 2)
    private BigDecimal percentual;

    public void calcularPercentual(BigDecimal totalRelatorio) {
        if (valorTotal == null || totalRelatorio == null || totalRelatorio.signum() == 0) {
            this.percentual = BigDecimal.ZERO.setScale(2);
            return;
        }
        this.percentual = valorTotal
                .multiply(CEM)
                .divide(totalRelatorio, 2, RoundingMode.HALF_UP);
    }
}
