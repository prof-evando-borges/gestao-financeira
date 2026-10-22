package br.com.fiap.gestaofinanceira.notificacoes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

@Entity
@Table(name = "TB_NTF_MODELO")
@Getter
@Setter
@NoArgsConstructor
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "CODIGO", nullable = false, length = 50)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Lob
    @Column(name = "CONTEUDO", nullable = false)
    private String conteudo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @NotNull
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    @Column(name = "CRIADO_EM", nullable = false, insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "modelo")
    private List<Notificacao> notificacoes = new ArrayList<>();
}
