package br.com.fiap.gestaofinanceira.notificacoes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.PrePersist;
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
    @NotBlank(message = "Informe o id da pessoa destinatária (idPessoa)")
    @Size(max = 36, message = "O id da pessoa deve ter no máximo 36 caracteres")
    @Column(name = "ID_PESSOA", nullable = false, length = 36)
    private String idPessoa;

    // Mesmo módulo: EAGER para aparecer no JSON (open-in-view desligado).
    // No cadastro basta mandar "canal": { "id": "..." } e, se quiser, "modelo": { "id": "..." }
    @NotNull(message = "O canal é obrigatório: informe \"canal\": { \"id\": \"...\" }")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_CANAL", nullable = false)
    private Canal canal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_MODELO")
    private Modelo modelo;

    @NotBlank(message = "O título da notificação é obrigatório")
    @Size(max = 150, message = "O título da notificação deve ter no máximo 150 caracteres")
    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "A mensagem da notificação é obrigatória")
    @Lob
    @Column(name = "MENSAGEM", nullable = false)
    private String mensagem;

    @NotNull(message = "O tipo de evento é obrigatório (ex.: PARCELA_DIVIDA, SALDO_BAIXO, SISTEMA)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @NotNull(message = "A prioridade é obrigatória: BAIXA, MEDIA ou ALTA")
    @Enumerated(EnumType.STRING)
    @Column(name = "PRIORIDADE", nullable = false, length = 10)
    private Prioridade prioridade = Prioridade.MEDIA;

    // O status muda pelo fluxo da notificação (envios e alteração de status), não pelo cadastro
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusNotificacao status = StatusNotificacao.PENDENTE;

    @NotNull(message = "A origem é obrigatória (módulo que gerou o evento, ex.: CONTAS, DIVIDAS)")
    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGEM", nullable = false, length = 30)
    private ModuloOrigem origem;

    // Id do registro do módulo de origem que gerou a notificação
    @Size(max = 36, message = "O id de referência deve ter no máximo 36 caracteres")
    @Column(name = "ID_REFERENCIA", length = 36)
    private String idReferencia;

    // Não pode estar no passado: conferido no NotificacaoService (só no cadastro e na edição)
    @Column(name = "AGENDADA_PARA")
    private LocalDateTime agendadaPara;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "ENVIADA_EM")
    private LocalDateTime enviadaEm;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "LIDA_EM")
    private LocalDateTime lidaEm;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "CRIADO_EM", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Lado @OneToMany: @JsonIgnore evita o loop Notificacao -> Envio -> Notificacao no JSON.
    // O histórico de envios sai junto quando a notificação é removida.
    @JsonIgnore
    @OneToMany(mappedBy = "notificacao", cascade = CascadeType.REMOVE)
    private List<Envio> envios = new ArrayList<>();

    @PrePersist
    void preencherCriadoEm() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
