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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CATEGORIA", nullable = false)
    private Categoria categoria;

    @NotBlank
    @Size(max = 36)
    @Column(name = "ID_PESSOA", nullable = false, length = 36)
    private String idPessoa;

    @Size(max = 36)
    @Column(name = "ID_CONTA", length = 36)
    private String idConta;

    @NotBlank
    @Size(max = 150)
    @Column(name = "DESCRICAO", nullable = false, length = 150)
    private String descricao;

    @NotNull
    @Positive
    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull
    @Column(name = "DATA_TRANSACAO", nullable = false)
    private LocalDate dataTransacao;

    @NotNull
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
