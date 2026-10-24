package br.com.fiap.gestaofinanceira.dividas.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_DIV_DIVIDA")
@Getter
@Setter
@NoArgsConstructor
public class Divida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TIPO", nullable = false)
    private TipoDivida tipo;

    // Tabela de OUTRO modulo (Pessoas): guarda so o id, sem @ManyToOne
    @NotNull
    @Positive
    @Column(name = "ID_PESSOA", nullable = false)
    private Long idPessoa;

    @NotBlank
    @Size(max = 150)
    @Column(name = "CREDOR", nullable = false, length = 150)
    private String credor;

    @Size(max = 255)
    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @NotNull
    @Positive
    @Column(name = "VALOR_TOTAL", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;

    @NotNull
    @PositiveOrZero
    @Column(name = "TAXA_JUROS", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxaJuros = BigDecimal.ZERO;

    @NotNull
    @Column(name = "DATA_CONTRATACAO", nullable = false)
    private LocalDate dataContratacao;

    @NotNull
    @Positive
    @Column(name = "QTD_PARCELAS", nullable = false)
    private Integer qtdParcelas;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusDivida status = StatusDivida.ATIVA;

    @Column(name = "CRIADO_EM", nullable = false, insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "divida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcela> parcelas = new ArrayList<>();
}
