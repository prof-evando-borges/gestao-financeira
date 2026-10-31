package br.com.fiap.gestaofinanceira.notificacoes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "O código do modelo é obrigatório")
    @Size(max = 50, message = "O código do modelo deve ter no máximo 50 caracteres")
    @Pattern(regexp = "[A-Z0-9_]+", message = "O código deve ter só letras maiúsculas, números e _ (ex.: PARCELA_VENCENDO)")
    @Column(name = "CODIGO", nullable = false, length = 50)
    private String codigo;

    @NotBlank(message = "O título do modelo é obrigatório")
    @Size(max = 150, message = "O título do modelo deve ter no máximo 150 caracteres")
    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "O conteúdo do modelo é obrigatório")
    @Lob
    @Column(name = "CONTEUDO", nullable = false)
    private String conteudo;

    @NotNull(message = "O tipo de evento é obrigatório (ex.: PARCELA_DIVIDA, SALDO_BAIXO, SISTEMA)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @NotNull(message = "Informe se o modelo está ativo (true ou false)")
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "CRIADO_EM", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Lado @OneToMany: @JsonIgnore evita o loop Modelo -> Notificacao -> Modelo no JSON
    @JsonIgnore
    @OneToMany(mappedBy = "modelo")
    private List<Notificacao> notificacoes = new ArrayList<>();

    @PrePersist
    void preencherCriadoEm() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
