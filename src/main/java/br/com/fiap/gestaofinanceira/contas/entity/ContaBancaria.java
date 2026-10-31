package br.com.fiap.gestaofinanceira.contas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Conta bancaria do usuario. Uma unica classe com o campo {@code tipo}
 * (CORRENTE ou POUPANCA): o limite de cheque especial so vale para corrente
 * e a taxa de rendimento so para poupanca.
 */
@Entity
@Table(name = "TB_CTA_CONTA")
@Getter
@Setter
@NoArgsConstructor
public class ContaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // Titular: TB_PES_USUARIO do modulo 01. Outro modulo -> so o id.
    @NotBlank(message = "O id do usuário titular é obrigatório")
    @Size(max = 36, message = "O id do usuário titular deve ter no máximo 36 caracteres")
    @Column(name = "ID_USUARIO", nullable = false, length = 36)
    private String idUsuario;

    // EAGER: o banco aparece no JSON e o open-in-view esta desligado.
    @NotNull(message = "O banco é obrigatório: informe \"banco\": { \"id\": \"...\" }")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_BANCO", nullable = false)
    private Banco banco;

    @NotBlank(message = "O número da conta é obrigatório")
    @Size(max = 20, message = "O número da conta deve ter no máximo 20 caracteres")
    @Pattern(regexp = "[0-9-]+", message = "O número da conta deve conter apenas dígitos e hífen")
    @Column(name = "NUMERO_CONTA", nullable = false, length = 20)
    private String numeroConta;

    @NotBlank(message = "A agência é obrigatória")
    @Size(max = 10, message = "A agência deve ter no máximo 10 caracteres")
    @Pattern(regexp = "[0-9-]+", message = "A agência deve conter apenas dígitos e hífen")
    @Column(name = "AGENCIA", nullable = false, length = 10)
    private String agencia;

    @NotNull(message = "O tipo da conta é obrigatório (CORRENTE ou POUPANCA)")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoConta tipo;

    @NotNull(message = "O saldo é obrigatório")
    @Digits(integer = 13, fraction = 2, message = "O saldo deve ter até 13 dígitos inteiros e 2 casas decimais")
    @Column(name = "SALDO", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @PositiveOrZero(message = "O limite do cheque especial não pode ser negativo")
    @Digits(integer = 13, fraction = 2, message = "O limite deve ter até 13 dígitos inteiros e 2 casas decimais")
    @Column(name = "LIMITE_CHEQUE_ESPECIAL", precision = 15, scale = 2)
    private BigDecimal limiteChequeEspecial;

    @DecimalMin(value = "0.00", message = "A taxa de rendimento não pode ser negativa")
    @Digits(integer = 3, fraction = 2, message = "A taxa de rendimento deve ter até 3 dígitos inteiros e 2 casas decimais")
    @Column(name = "TAXA_RENDIMENTO_MENSAL", precision = 5, scale = 2)
    private BigDecimal taxaRendimentoMensal;

    @NotNull(message = "O status da conta é obrigatório (ATIVA, BLOQUEADA ou ENCERRADA)")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusConta status = StatusConta.ATIVA;

    @NotNull(message = "A data de abertura é obrigatória")
    @PastOrPresent(message = "A data de abertura não pode estar no futuro")
    @Column(name = "DATA_ABERTURA", nullable = false)
    private LocalDate dataAbertura;

    @PastOrPresent(message = "A data de encerramento não pode estar no futuro")
    @Column(name = "DATA_ENCERRAMENTO")
    private LocalDate dataEncerramento;

    // Lado @OneToMany fica fora do JSON para nao entrar em loop (conta -> chave -> conta).
    @JsonIgnore
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChavePix> chavesPix = new ArrayList<>();
}
