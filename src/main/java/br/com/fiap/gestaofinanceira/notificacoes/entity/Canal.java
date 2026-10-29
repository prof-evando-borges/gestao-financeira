package br.com.fiap.gestaofinanceira.notificacoes.entity;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "NOME", nullable = false, length = 30)
    private NomeCanal nome;

    @Size(max = 200)
    @Column(name = "DESCRICAO", length = 200)
    private String descricao;

    @NotNull
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "canal")
    private List<Preferencia> preferencias = new ArrayList<>();

    @OneToMany(mappedBy = "canal")
    private List<Notificacao> notificacoes = new ArrayList<>();
}
