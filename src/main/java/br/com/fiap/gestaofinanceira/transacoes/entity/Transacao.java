package br.com.fiap.gestaofinanceira.transacoes.entity;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Receita ou Despesa (discriminador: coluna TIPO).
 * ID_PESSOA (modulo 01) e ID_CONTA (modulo 02) sao guardados apenas como id,
 * sem importar entidades de outros pacotes.
 */
@Entity
@Table(name = "TB_TRN_TRANSACAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "categoria")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    @EqualsAndHashCode.Include
    private String id;

    @NotNull(message = "A categoria é obrigatória: informe \"categoria\": { \"id\": \"...\" }")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_CATEGORIA", nullable = false)
    private Categoria categoria;

    @NotBlank(message = "O id da pessoa é obrigatório")
    @Size(max = 36, message = "O id da pessoa deve ter no máximo 36 caracteres")
    @Column(name = "ID_PESSOA", nullable = false, length = 36)
    private String idPessoa;

    @Size(max = 36, message = "O id da conta deve ter no máximo 36 caracteres")
    @Column(name = "ID_CONTA", length = 36)
    private String idConta;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 150, message = "A descrição deve ter no máximo 150 caracteres")
    @Column(name = "DESCRICAO", nullable = false, length = 150)
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "A data da transação é obrigatória")
    @Column(name = "DATA_TRANSACAO", nullable = false)
    private LocalDate dataTransacao;

    @NotNull(message = "O tipo da transação é obrigatório (RECEITA ou DESPESA)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 10)
    private TipoTransacao tipo;

    /** Somente para despesas (null em receitas). */
    @Column(name = "PAGA")
    private Boolean paga;

    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    void aoCriar() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    /** Efeito no saldo: receita soma, despesa subtrai. */
    public BigDecimal efeitoNoSaldo() {
        return tipo == TipoTransacao.DESPESA ? valor.negate() : valor;
    }
}
