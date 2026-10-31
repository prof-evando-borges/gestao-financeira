package br.com.fiap.gestaofinanceira.educacao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("VIDEO")
@Getter
@Setter
@NoArgsConstructor
public class Video extends ConteudoEducacional {

    @NotBlank(message = "A URL do vídeo é obrigatória")
    @Pattern(regexp = "https?://.+", message = "A URL do vídeo deve começar com http:// ou https://")
    @Size(max = 300, message = "A URL do vídeo deve ter no máximo 300 caracteres")
    @Column(name = "URL_VIDEO", length = 300)
    private String urlVideo;

    @Override
    public String exibirDetalhes() {
        return "Vídeo disponível em " + urlVideo;
    }

    @Override
    public void atualizarDadosEspecificos(ConteudoEducacional dados) {
        this.urlVideo = ((Video) dados).getUrlVideo();
    }
}
