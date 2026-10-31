package br.com.fiap.gestaofinanceira.notificacoes.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import jakarta.persistence.PreUpdate;
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
    @NotBlank(message = "Informe o id da pessoa (idPessoa)")
    @Size(max = 36, message = "O id da pessoa deve ter no máximo 36 caracteres")
    @Column(name = "ID_PESSOA", nullable = false, length = 36)
    private String idPessoa;

    // Mesmo módulo: EAGER para o canal aparecer no JSON (open-in-view desligado).
    // No cadastro basta mandar "canal": { "id": "..." }
    @NotNull(message = "O canal é obrigatório: informe \"canal\": { \"id\": \"...\" }")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_CANAL", nullable = false)
    private Canal canal;

    @NotNull(message = "O tipo de evento é obrigatório (ex.: PARCELA_DIVIDA, SALDO_BAIXO, SISTEMA)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @NotNull(message = "Informe se a preferência está habilitada (true ou false)")
    @Column(name = "HABILITADO", nullable = false)
    private Boolean habilitado = true;

    @NotNull(message = "A antecedência em dias é obrigatória")
    @Min(value = 0, message = "A antecedência não pode ser menor que 0 dias")
    @Max(value = 30, message = "A antecedência não pode ser maior que 30 dias")
    @Column(name = "ANTECEDENCIA_DIAS", nullable = false)
    private Integer antecedenciaDias = 0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "ATUALIZADO_EM", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    void preencherAtualizadoEm() {
        atualizadoEm = LocalDateTime.now();
    }
}
