package br.com.fiap.gestaofinanceira.notificacoes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_NTF_CANAL")
@Getter
@Setter
@NoArgsConstructor
public class Canal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    @NotNull(message = "O nome do canal é obrigatório: EMAIL, SMS, PUSH ou APP")
    @Enumerated(EnumType.STRING)
    @Column(name = "NOME", nullable = false, length = 30)
    private NomeCanal nome;

    @Size(max = 200, message = "A descrição do canal deve ter no máximo 200 caracteres")
    @Column(name = "DESCRICAO", length = 200)
    private String descricao;

    @NotNull(message = "Informe se o canal está ativo (true ou false)")
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    // Lado @OneToMany: @JsonIgnore evita o loop Canal -> Preferencia -> Canal no JSON
    @JsonIgnore
    @OneToMany(mappedBy = "canal")
    private List<Preferencia> preferencias = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "canal")
    private List<Notificacao> notificacoes = new ArrayList<>();
}
