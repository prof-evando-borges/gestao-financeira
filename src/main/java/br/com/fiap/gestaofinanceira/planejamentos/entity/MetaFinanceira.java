package br.com.fiap.gestaofinanceira.planejamentos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_PLN_META")
public class MetaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // Id de outro modulo (Cadastro Pessoas, tabela TB_PUSUARIO): guarda apenas o id
    @NotBlank(message = "O id do usuário é obrigatório")
    @Size(max = 36, message = "O id do usuário deve ter no máximo 36 caracteres")
    @Column(name = "ID_USUARIO", length = 36, nullable = false)
    private String idUsuario;

    @NotBlank(message = "O nome da meta é obrigatório")
    @Size(max = 100, message = "O nome da meta deve ter no máximo 100 caracteres")
    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @NotNull(message = "O valor objetivo é obrigatório")
    @Positive(message = "O valor objetivo deve ser maior que zero")
    @Column(name = "VALOR_OBJETIVO", precision = 15, scale = 2, nullable = false)
    private BigDecimal valorObjetivo;

    @NotNull(message = "A data de criação é obrigatória")
    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDate dataCriacao;

    @NotNull(message = "A data limite é obrigatória")
    @Column(name = "DATA_LIMITE", nullable = false)
    private LocalDate dataLimite;

    @JsonIgnore
    @OneToMany(mappedBy = "meta")
    private List<Aporte> aportes = new ArrayList<>();

    public MetaFinanceira() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorObjetivo() {
        return valorObjetivo;
    }

    public void setValorObjetivo(BigDecimal valorObjetivo) {
        this.valorObjetivo = valorObjetivo;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    public List<Aporte> getAportes() {
        return aportes;
    }

    public void setAportes(List<Aporte> aportes) {
        this.aportes = aportes;
    }
}