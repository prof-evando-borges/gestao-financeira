package br.com.fiap.gestaofinanceira.educacao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ARTIGO")
@Getter
@Setter
@NoArgsConstructor
public class Artigo extends ConteudoEducacional {

    @NotBlank(message = "O autor do artigo é obrigatório")
    @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres")
    @Column(name = "AUTOR", length = 100)
    private String autor;

    @Override
    public String exibirDetalhes() {
        return "Artigo escrito por " + autor;
    }

    @Override
    public void atualizarDadosEspecificos(ConteudoEducacional dados) {
        this.autor = ((Artigo) dados).getAutor();
    }
}
