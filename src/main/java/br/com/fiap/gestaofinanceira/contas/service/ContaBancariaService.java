package br.com.fiap.gestaofinanceira.contas.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.contas.entity.Banco;
import br.com.fiap.gestaofinanceira.contas.entity.ContaBancaria;
import br.com.fiap.gestaofinanceira.contas.entity.StatusConta;
import br.com.fiap.gestaofinanceira.contas.entity.TipoConta;
import br.com.fiap.gestaofinanceira.contas.exception.ContaException;
import br.com.fiap.gestaofinanceira.contas.repository.ContaBancariaRepository;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras das contas bancarias. Os outros modulos usam {@link #buscarPorId(String)}
 * para conferir uma conta e {@link #debitar}/{@link #creditar} para movimentar o saldo.
 */
@Service
public class ContaBancariaService {

    private final ContaBancariaRepository contaBancariaRepository;
    private final BancoService bancoService;
    // Modulo 01 (Pessoas): o titular e conferido pelo Service do grupo dono, nunca pelo Repository dele
    private final UsuarioService usuarioService;

    public ContaBancariaService(ContaBancariaRepository contaBancariaRepository,
                                BancoService bancoService,
                                UsuarioService usuarioService) {
        this.contaBancariaRepository = contaBancariaRepository;
        this.bancoService = bancoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public ContaBancaria cadastrar(ContaBancaria conta) {
        usuarioService.buscarPorId(conta.getIdUsuario()); // 404 se o titular nao existir
        Banco banco = bancoService.buscarPorId(idDoBanco(conta)); // 404 se o banco nao existir

        if (conta.getStatus() == StatusConta.ENCERRADA) {
            throw new ContaException("Não é possível cadastrar uma conta já ENCERRADA");
        }
        if (conta.getSaldo().signum() < 0) {
            throw new ContaException("O saldo inicial da conta não pode ser negativo");
        }
        validarCamposDoTipo(conta);
        validarContaRepetida(banco.getId(), conta.getAgencia(), conta.getNumeroConta(), null);

        conta.setId(null);
        conta.setBanco(banco);
        conta.setDataEncerramento(null);
        return contaBancariaRepository.save(conta);
    }

    public List<ContaBancaria> listarTodos() {
        return contaBancariaRepository.findAll();
    }

    public List<ContaBancaria> listar(String idUsuario, StatusConta status) {
        if (idUsuario != null && status != null) {
            return contaBancariaRepository.findByIdUsuarioAndStatus(idUsuario, status);
        }
        if (idUsuario != null) {
            return contaBancariaRepository.findByIdUsuario(idUsuario);
        }
        if (status != null) {
            return contaBancariaRepository.findByStatus(status);
        }
        return listarTodos();
    }

    public ContaBancaria buscarPorId(String id) {
        if (id == null) {
            throw new RecursoNaoEncontradoException("Informe o id da conta bancária");
        }
        return contaBancariaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta bancária não encontrada: " + id));
    }

    @Transactional
    public ContaBancaria atualizar(String id, ContaBancaria dados) {
        ContaBancaria conta = buscarPorId(id);
        // Regra: conta encerrada nao pode mais ser alterada nem reaberta
        if (conta.getStatus() == StatusConta.ENCERRADA) {
            throw new ContaException("A conta " + id + " está ENCERRADA e não pode ser alterada");
        }
        usuarioService.buscarPorId(dados.getIdUsuario());
        Banco banco = bancoService.buscarPorId(idDoBanco(dados));
        validarCamposDoTipo(dados);
        validarContaRepetida(banco.getId(), dados.getAgencia(), dados.getNumeroConta(), id);
        // O saldo nao muda pelo PUT (so por debitar/creditar), mas precisa caber no novo limite
        validarSaldoDentroDoLimite(conta.getSaldo(), dados.getLimiteChequeEspecial());

        conta.setIdUsuario(dados.getIdUsuario());
        conta.setBanco(banco);
        conta.setAgencia(dados.getAgencia());
        conta.setNumeroConta(dados.getNumeroConta());
        conta.setTipo(dados.getTipo());
        conta.setLimiteChequeEspecial(dados.getLimiteChequeEspecial());
        conta.setTaxaRendimentoMensal(dados.getTaxaRendimentoMensal());
        conta.setDataAbertura(dados.getDataAbertura());
        conta.setStatus(dados.getStatus());

        if (dados.getStatus() == StatusConta.ENCERRADA) {
            encerrar(conta, dados.getDataEncerramento());
        } else {
            conta.setDataEncerramento(null);
        }
        return contaBancariaRepository.save(conta);
    }

    @Transactional
    public void remover(String id) {
        ContaBancaria conta = buscarPorId(id);
        if (conta.getSaldo().signum() != 0) {
            throw new ContaException("Só é possível remover uma conta com saldo zero. Saldo atual: " + conta.getSaldo());
        }
        contaBancariaRepository.delete(conta);
        try {
            // Forca o DELETE agora para descobrir se outro modulo (ex.: parcelas de Dividas) usa a conta
            contaBancariaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ContaException("A conta está vinculada a registros de outros módulos e não pode ser removida; "
                    + "encerre a conta pelo PUT com status ENCERRADA", e);
        }
    }

    /**
     * Tira dinheiro da conta. Para os modulos de Transacoes, Investimentos e Dividas.
     * Regra: o saldo pode ficar negativo no maximo ate o limite do cheque especial.
     */
    @Transactional
    public ContaBancaria debitar(String idConta, BigDecimal valor) {
        ContaBancaria conta = buscarContaParaMovimentar(idConta, valor);
        BigDecimal novoSaldo = conta.getSaldo().subtract(valor);
        if (novoSaldo.compareTo(limiteDe(conta).negate()) < 0) {
            throw new ContaException("Saldo insuficiente: saldo de " + conta.getSaldo() + " mais limite de "
                    + limiteDe(conta) + " não cobre o débito de " + valor);
        }
        conta.setSaldo(novoSaldo);
        return contaBancariaRepository.save(conta);
    }

    /**
     * Coloca dinheiro na conta. Para os modulos de Transacoes e Investimentos.
     */
    @Transactional
    public ContaBancaria creditar(String idConta, BigDecimal valor) {
        ContaBancaria conta = buscarContaParaMovimentar(idConta, valor);
        conta.setSaldo(conta.getSaldo().add(valor));
        return contaBancariaRepository.save(conta);
    }

    private ContaBancaria buscarContaParaMovimentar(String idConta, BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new ContaException("O valor da movimentação deve ser maior que zero");
        }
        ContaBancaria conta = buscarPorId(idConta);
        // Regra: so conta ATIVA movimenta dinheiro (BLOQUEADA e ENCERRADA nao)
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new ContaException("A conta " + idConta + " está " + conta.getStatus() + " e não pode ser movimentada");
        }
        return conta;
    }

    private void encerrar(ContaBancaria conta, LocalDate dataEncerramento) {
        // Regra: so encerra conta zerada
        if (conta.getSaldo().signum() != 0) {
            throw new ContaException("Só é possível encerrar uma conta com saldo zero. Saldo atual: " + conta.getSaldo());
        }
        LocalDate data = dataEncerramento != null ? dataEncerramento : LocalDate.now();
        if (data.isBefore(conta.getDataAbertura())) {
            throw new ContaException("A data de encerramento não pode ser anterior à data de abertura");
        }
        conta.setDataEncerramento(data);
        // Conta encerrada nao recebe Pix
        conta.getChavesPix().forEach(chave -> chave.setAtiva(false));
    }

    private void validarCamposDoTipo(ContaBancaria conta) {
        if (conta.getTipo() == TipoConta.POUPANCA && conta.getLimiteChequeEspecial() != null) {
            throw new ContaException("Conta POUPANCA não tem cheque especial: deixe limiteChequeEspecial vazio");
        }
        if (conta.getTipo() == TipoConta.CORRENTE && conta.getTaxaRendimentoMensal() != null) {
            throw new ContaException("Conta CORRENTE não tem rendimento: deixe taxaRendimentoMensal vazio");
        }
    }

    private void validarContaRepetida(String idBanco, String agencia, String numeroConta, String idIgnorado) {
        boolean repetida = idIgnorado == null
                ? contaBancariaRepository.existsByBancoIdAndAgenciaAndNumeroConta(idBanco, agencia, numeroConta)
                : contaBancariaRepository.existsByBancoIdAndAgenciaAndNumeroContaAndIdNot(idBanco, agencia, numeroConta, idIgnorado);
        if (repetida) {
            throw new ContaException("Já existe uma conta com a agência " + agencia + " e o número "
                    + numeroConta + " neste banco");
        }
    }

    private void validarSaldoDentroDoLimite(BigDecimal saldo, BigDecimal limite) {
        BigDecimal piso = limite == null ? BigDecimal.ZERO : limite.negate();
        if (saldo.compareTo(piso) < 0) {
            throw new ContaException("O saldo atual de " + saldo + " ultrapassa o limite do cheque especial informado");
        }
    }

    private BigDecimal limiteDe(ContaBancaria conta) {
        return conta.getLimiteChequeEspecial() == null ? BigDecimal.ZERO : conta.getLimiteChequeEspecial();
    }

    private String idDoBanco(ContaBancaria conta) {
        if (conta.getBanco() == null || conta.getBanco().getId() == null) {
            throw new ContaException("Informe o banco da conta: \"banco\": { \"id\": \"...\" }");
        }
        return conta.getBanco().getId();
    }
}
