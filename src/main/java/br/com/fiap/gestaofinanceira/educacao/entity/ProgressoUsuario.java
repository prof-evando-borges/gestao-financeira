package br.com.fiap.gestaofinanceira.educacao.entity;

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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Progresso de um usuário (módulo 01 - Pessoas) em um conteúdo educacional.
 */
@Entity
@Table(name = "TB_EDU_PROGRESSO")
@Getter
@Setter
@NoArgsConstructor
public class ProgressoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // Tabela de OUTRO modulo (01 - Pessoas, TB_PES_USUARIO): guarda so o id, sem @ManyToOne
    @NotBlank(message = "Informe o id do usuário (idUsuario)")
    @Size(max = 36, message = "O id do usuário deve ter no máximo 36 caracteres")
    @Column(name = "ID_USUARIO", nullable = false, length = 36)
    private String idUsuario;

    // Mesmo modulo: @ManyToOne EAGER para o conteudo aparecer no JSON (open-in-view desligado).
    // So aparece na resposta; no corpo da requisicao o conteudo vem pelo campo "idConteudo".
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_CONTEUDO", nullable = false)
    private ConteudoEducacional conteudo;

    // Como ConteudoEducacional e abstrata, "conteudo": { "id": "..." } exigiria tambem o "tipo".
    // Por isso o cadastro recebe so o id; o Service busca o conteudo antes de salvar.
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Informe o id do conteúdo (idConteudo)")
    private String idConteudo;

    @NotNull(message = "Informe o status: NAO_INICIADO, EM_ANDAMENTO ou CONCLUIDO")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusProgresso status;

    @NotNull(message = "O percentual concluído é obrigatório")
    @Min(value = 0, message = "O percentual não pode ser menor que 0")
    @Max(value = 100, message = "O percentual não pode ser maior que 100")
    @Column(name = "PERCENTUAL", nullable = false)
    private Integer percentual;

    @NotNull(message = "A data de início é obrigatória (aaaa-mm-dd)")
    @PastOrPresent(message = "A data de início não pode estar no futuro")
    @Column(name = "DATA_INICIO", nullable = false)
    private LocalDate dataInicio;

    @PastOrPresent(message = "A data de conclusão não pode estar no futuro")
    @Column(name = "DATA_CONCLUSAO")
    private LocalDate dataConclusao;
}
