package br.com.fiap.gestaofinanceira.notificacoes.entity;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_NTF_PREFERENCIA")
@Getter
@Setter
@NoArgsConstructor
public class Preferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // Tabela de outro módulo (01 - Cadastro Pessoas): guarda só o id
    @NotBlank
    @Size(max = 36)
    @Column(name = "ID_PESSOA", nullable = false, length = 36)
    private String idPessoa;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CANAL", nullable = false)
    private Canal canal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @NotNull
    @Column(name = "HABILITADO", nullable = false)
    private Boolean habilitado = true;

    @NotNull
    @Min(0)
    @Max(30)
    @Column(name = "ANTECEDENCIA_DIAS", nullable = false)
    private Integer antecedenciaDias = 0;

    @Column(name = "ATUALIZADO_EM", nullable = false, insertable = false, updatable = false)
    private LocalDateTime atualizadoEm;
}
