package br.com.fiap.gestaofinanceira.educacao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CURSO")
@Getter
@Setter
@NoArgsConstructor
public class CursoOnline extends ConteudoEducacional {

    @NotNull(message = "O número de módulos do curso é obrigatório")
    @Positive(message = "O curso deve ter pelo menos 1 módulo")
    @Max(value = 100, message = "O curso pode ter no máximo 100 módulos")
    @Column(name = "NUMERO_MODULOS")
    private Integer numeroModulos;

    @Override
    public String exibirDetalhes() {
        return "Curso online com " + numeroModulos + " módulo(s)";
    }

    @Override
    public void atualizarDadosEspecificos(ConteudoEducacional dados) {
        this.numeroModulos = ((CursoOnline) dados).getNumeroModulos();
    }
}
