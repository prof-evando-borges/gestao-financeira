package br.com.fiap.gestaofinanceira.investimentos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Aplicacao financeira de um investidor, feita a partir de uma conta bancaria do modulo 02.
 * No cadastro, o investidor e informado so pelo id: "investidor": { "id": "..." }.
 */
@Entity
@Table(name = "TB_INV_APLICACAO")
@Getter
@Setter
@NoArgsConstructor
public class Aplicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // Mesmo modulo: relacionamento JPA. EAGER porque o investidor aparece na resposta (open-in-view desligado)
    @NotNull(message = "Informe o investidor da aplicação (campo investidor.id).")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_INVESTIDOR", nullable = false)
    private Investidor investidor;

    // Tabela de OUTRO modulo (02 - Contas, TB_CTA_CONTA): guarda so o id, sem @ManyToOne
    @NotBlank(message = "Informe o id da conta bancária de origem (idConta).")
    @Size(max = 36, message = "O id da conta deve ter no máximo 36 caracteres.")
    @Column(name = "ID_CONTA", nullable = false, length = 36)
    private String idConta;

    @NotBlank(message = "Informe a descrição da aplicação.")
    @Size(max = 150, message = "A descrição deve ter no máximo 150 caracteres.")
    @Column(name = "DESCRICAO", nullable = false, length = 150)
    private String descricao;

    @NotNull(message = "Informe o tipo: TESOURO_DIRETO, RENDA_FIXA, FUNDOS, ACOES ou CRIPTOMOEDAS.")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 30)
    private TipoInvestimento tipo;

    @NotNull(message = "Informe o valor aplicado.")
    @Positive(message = "O valor aplicado deve ser maior que zero.")
    @Digits(integer = 13, fraction = 2, message = "O valor aplicado aceita até 13 dígitos inteiros e 2 casas decimais.")
    @Column(name = "VALOR_APLICADO", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAplicado;

    @NotNull(message = "Informe a taxa anual esperada (% a.a.).")
    @PositiveOrZero(message = "A taxa anual não pode ser negativa.")
    @Digits(integer = 4, fraction = 2, message = "A taxa anual aceita até 4 dígitos inteiros e 2 casas decimais.")
    @Column(name = "TAXA_ANUAL", nullable = false, precision = 6, scale = 2)
    private BigDecimal taxaAnual;

    @NotNull(message = "Informe a data da aplicação.")
    @PastOrPresent(message = "A data da aplicação não pode estar no futuro.")
    @Column(name = "DATA_APLICACAO", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "DATA_VENCIMENTO")
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusAplicacao status = StatusAplicacao.ATIVA;

    @Column(name = "CRIADO_EM", nullable = false, insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    /**
     * Confere que o corpo trouxe o id do investidor, e nao so um objeto vazio.
     * Fora do JSON e ignorado pelo JPA (a entidade usa acesso por atributo).
     */
    @JsonIgnore
    @AssertTrue(message = "Informe o id do investidor (campo investidor.id).")
    public boolean isIdInvestidorInformado() {
        return investidor == null || (investidor.getId() != null && !investidor.getId().isBlank());
    }

    /**
     * Montante projetado por juros compostos: M = C * (1 + i)^t, com i ao ano e t em anos (meses / 12).
     */
    public BigDecimal projetarMontante(int meses) {
        double fator = Math.pow(1 + taxaAnual.doubleValue() / 100, meses / 12.0);
        return valorAplicado.multiply(BigDecimal.valueOf(fator)).setScale(2, RoundingMode.HALF_EVEN);
    }
}
