package br.com.fiap.gestaofinanceira.educacao.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Conteúdo de educação financeira (classe abstrata do UML).
 *
 * <p>Banco: as três subclasses ficam na mesma tabela (SINGLE_TABLE) e a coluna TIPO
 * diz qual é a subclasse.</p>
 *
 * <p>JSON: o campo "tipo" (ARTIGO, VIDEO ou CURSO) diz ao Jackson qual subclasse criar
 * no cadastro e aparece nas respostas. Ex.: { "tipo": "VIDEO", "titulo": "...", "urlVideo": "..." }</p>
 */
@Entity
@Table(name = "TB_EDU_CONTEUDO")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO", discriminatorType = DiscriminatorType.STRING, length = 20)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Artigo.class, name = "ARTIGO"),
        @JsonSubTypes.Type(value = Video.class, name = "VIDEO"),
        @JsonSubTypes.Type(value = CursoOnline.class, name = "CURSO")
})
@Getter
@Setter
@NoArgsConstructor
public abstract class ConteudoEducacional {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    @NotBlank(message = "O título do conteúdo é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column(name = "DESCRICAO", length = 500)
    private String descricao;

    @NotNull(message = "Informe o nível do conteúdo: INICIANTE, INTERMEDIARIO ou AVANCADO")
    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL", nullable = false, length = 20)
    private NivelConhecimento nivel;

    // Opcional no cadastro: se nao vier, o Service grava como ativo
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo;

    /** Texto curto com os dados específicos do tipo (exibirDetalhes do UML). */
    public abstract String exibirDetalhes();

    /** Copia para esta instância os campos específicos da subclasse (usado na atualização). */
    public abstract void atualizarDadosEspecificos(ConteudoEducacional dados);
}
