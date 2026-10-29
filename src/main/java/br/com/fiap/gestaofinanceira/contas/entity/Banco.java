package br.com.fiap.gestaofinanceira.contas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Instituicao financeira onde a conta e mantida, identificada pelo codigo COMPE.
 */
@Entity
@Table(name = "TB_CTA_BANCO")
@Getter
@Setter
@NoArgsConstructor
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    @NotBlank(message = "O código do banco é obrigatório")
    @Pattern(regexp = "\\d{3}", message = "O código do banco deve ter 3 dígitos (ex.: 001)")
    @Column(name = "CODIGO", nullable = false, length = 3)
    private String codigo;

    @NotBlank(message = "O nome do banco é obrigatório")
    @Size(max = 100, message = "O nome do banco deve ter no máximo 100 caracteres")
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;
}
