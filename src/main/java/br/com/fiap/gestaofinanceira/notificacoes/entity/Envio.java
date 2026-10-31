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
import jakarta.persistence.ManyToOne;
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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_NOTIFICACAO", nullable = false)
    private Notificacao notificacao;

    @NotNull
    @Positive
    @Column(name = "NUMERO_TENTATIVA", nullable = false)
    private Integer numeroTentativa;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusEnvio status;

    @Size(max = 500)
    @Column(name = "MENSAGEM_ERRO", length = 500)
    private String mensagemErro;

    @Column(name = "DATA_ENVIO", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataEnvio;
}
