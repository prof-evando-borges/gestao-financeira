package br.com.fiap.gestaofinanceira.investimentos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Perfil de investidor de uma pessoa do modulo 01. Uma pessoa tem no maximo um perfil.
 */
@Entity
@Table(name = "TB_INV_INVESTIDOR")
@Getter
@Setter
@NoArgsConstructor
public class Investidor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // Tabela de OUTRO modulo (01 - Pessoas, TB_PES_USUARIO): guarda so o id, sem @ManyToOne
    @NotBlank(message = "Informe o id da pessoa (idPessoa) que será o investidor.")
    @Size(max = 36, message = "O id da pessoa deve ter no máximo 36 caracteres.")
    @Column(name = "ID_PESSOA", nullable = false, length = 36)
    private String idPessoa;

    @NotNull(message = "Informe o perfil do investidor: CONSERVADOR, MODERADO ou ARROJADO.")
    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL", nullable = false, length = 20)
    private PerfilInvestidor perfil;

    @Size(max = 150, message = "O objetivo deve ter no máximo 150 caracteres.")
    @Column(name = "OBJETIVO", length = 150)
    private String objetivo;

    @Column(name = "CRIADO_EM", nullable = false, insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Lado @OneToMany: fora do JSON para nao entrar em loop (Investidor -> Aplicacao -> Investidor...)
    @JsonIgnore
    @OneToMany(mappedBy = "investidor", cascade = CascadeType.REMOVE)
    private List<Aplicacao> aplicacoes = new ArrayList<>();
}
