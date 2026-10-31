package br.com.fiap.gestaofinanceira.notificacoes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_NTF_ENVIO")
@Getter
@Setter
@NoArgsConstructor
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // No JSON sai só o idNotificacao (getter abaixo); a notificação inteira fica de fora
    @JsonIgnore
    @NotNull(message = "A notificação do envio é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_NOTIFICACAO", nullable = false)
    private Notificacao notificacao;

    @NotNull(message = "O número da tentativa é obrigatório")
    @Positive(message = "O número da tentativa deve ser maior que zero")
    @Column(name = "NUMERO_TENTATIVA", nullable = false)
    private Integer numeroTentativa;

    @NotNull(message = "O status do envio é obrigatório: SUCESSO ou FALHA")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusEnvio status;

    @Size(max = 500, message = "A mensagem de erro deve ter no máximo 500 caracteres")
    @Column(name = "MENSAGEM_ERRO", length = 500)
    private String mensagemErro;

    @Column(name = "DATA_ENVIO", nullable = false, updatable = false)
    private LocalDateTime dataEnvio;

    @JsonProperty("idNotificacao")
    public String getIdNotificacao() {
        return notificacao != null ? notificacao.getId() : null;
    }

    @PrePersist
    void preencherDataEnvio() {
        if (dataEnvio == null) {
            dataEnvio = LocalDateTime.now();
        }
    }
}
