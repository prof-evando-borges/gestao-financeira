package br.com.fiap.gestaofinanceira.dividas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_DIV_TIPO")
@Getter
@Setter
@NoArgsConstructor
public class TipoDivida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "NOME", nullable = false, length = 100, unique = true)
    private String nome;

    @Size(max = 255)
    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @OneToMany(mappedBy = "tipo")
    private List<Divida> dividas = new ArrayList<>();
}
