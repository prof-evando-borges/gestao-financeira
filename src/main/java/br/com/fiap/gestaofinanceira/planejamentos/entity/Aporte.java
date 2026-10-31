package br.com.fiap.gestaofinanceira.planejamentos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TB_PLN_APORTE")
public class Aporte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    @NotNull(message = "A meta é obrigatória")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_META", nullable = false)
    private MetaFinanceira meta;

    @NotNull(message = "O valor do aporte é obrigatório")
    @Positive(message = "O valor do aporte deve ser maior que zero")
    @Column(name = "VALOR", precision = 15, scale = 2, nullable = false)
    private BigDecimal valor;

    @NotNull(message = "A data do aporte é obrigatória")
    @Column(name = "DATA_APORTE", nullable = false)
    private LocalDate dataAporte;

    public Aporte() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MetaFinanceira getMeta() {
        return meta;
    }

    public void setMeta(MetaFinanceira meta) {
        this.meta = meta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataAporte() {
        return dataAporte;
    }

    public void setDataAporte(LocalDate dataAporte) {
        this.dataAporte = dataAporte;
    }
}