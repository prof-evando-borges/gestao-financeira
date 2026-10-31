package br.com.fiap.gestaofinanceira.notificacoes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
@Table(name = "TB_NTF_NOTIFICACAO")
@Getter
@Setter
@NoArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // Tabela de outro módulo (01 - Cadastro Pessoas): guarda só o id
    @NotBlank
    @Size(max = 36)
    @Column(name = "ID_PESSOA", nullable = false, length = 36)
    private String idPessoa;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CANAL", nullable = false)
    private Canal canal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MODELO")
    private Modelo modelo;

    @NotBlank
    @Size(max = 150)
    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Lob
    @Column(name = "MENSAGEM", nullable = false)
    private String mensagem;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "PRIORIDADE", nullable = false, length = 10)
    private Prioridade prioridade = Prioridade.MEDIA;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusNotificacao status = StatusNotificacao.PENDENTE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGEM", nullable = false, length = 30)
    private ModuloOrigem origem;

    // Id do registro do módulo de origem que gerou a notificação
    @Size(max = 36)
    @Column(name = "ID_REFERENCIA", length = 36)
    private String idReferencia;

    @Column(name = "AGENDADA_PARA")
    private LocalDateTime agendadaPara;

    @Column(name = "ENVIADA_EM")
    private LocalDateTime enviadaEm;

    @Column(name = "LIDA_EM")
    private LocalDateTime lidaEm;

    @Column(name = "CRIADO_EM", nullable = false, insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "notificacao")
    private List<Envio> envios = new ArrayList<>();
}
