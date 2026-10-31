package br.com.fiap.gestaofinanceira.transacoes.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.contas.entity.ContaBancaria;
import br.com.fiap.gestaofinanceira.contas.exception.ContaException;
import br.com.fiap.gestaofinanceira.contas.service.ContaBancariaService;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import br.com.fiap.gestaofinanceira.transacoes.entity.Categoria;
import br.com.fiap.gestaofinanceira.transacoes.entity.TipoTransacao;
import br.com.fiap.gestaofinanceira.transacoes.entity.Transacao;
import br.com.fiap.gestaofinanceira.transacoes.exception.TransacaoException;
import br.com.fiap.gestaofinanceira.transacoes.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Regras das transacoes. Receita sempre esta efetivada (credita a conta);
 * despesa so movimenta a conta quando esta paga.
 */
@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaService categoriaService;
    // Modulos 01 (Pessoas) e 02 (Contas): conferidos pelo Service do grupo dono, nunca pelo Repository
    private final UsuarioService usuarioService;
    private final ContaBancariaService contaBancariaService;

    public TransacaoService(TransacaoRepository transacaoRepository,
                            CategoriaService categoriaService,
                            UsuarioService usuarioService,
                            ContaBancariaService contaBancariaService) {
        this.transacaoRepository = transacaoRepository;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.contaBancariaService = contaBancariaService;
    }

    @Transactional
    public Transacao cadastrar(Transacao transacao) {
        validarEPreparar(transacao);
        transacao.setId(null); // o id e sempre gerado pela aplicacao
        Transacao salva = transacaoRepository.save(transacao);
        if (efetivada(salva)) {
            movimentarConta(salva, false);
        }
        return salva;
    }

    public List<Transacao> listarTodos() {
        return transacaoRepository.findAll();
    }

    public List<Transacao> listar(TipoTransacao tipo, String idPessoa) {
        if (tipo != null) {
            List<Transacao> porTipo = transacaoRepository.findByTipoOrderByDataTransacao(tipo);
            if (idPessoa == null) {
                return porTipo;
            }
            return porTipo.stream().filter(t -> idPessoa.equals(t.getIdPessoa())).toList();
        }
        if (idPessoa != null) {
            return transacaoRepository.findByIdPessoa(idPessoa);
        }
        return listarTodos();
    }

    /**
     * Contrato com os outros modulos: lanca 404 se a transacao nao existir.
     */
    public Transacao buscarPorId(String id) {
        if (id == null) {
            throw new RecursoNaoEncontradoException("Informe o id da transação");
        }
        return transacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação não encontrada com o id " + id));
    }

    @Transactional
    public Transacao atualizar(String id, Transacao dados) {
        Transacao transacao = buscarPorId(id);

        validarEPreparar(dados);

        // Regra: transacao efetivada ja mexeu no saldo; so descricao, categoria e data podem mudar
        if (efetivada(transacao)) {
            if (alteraCamposDoSaldo(transacao, dados)) {
                throw new TransacaoException("A transação " + id + " já foi efetivada: só é possível alterar "
                        + "descrição, categoria e data (pessoa, conta, valor, tipo e paga não mudam)");
            }
            transacao.setCategoria(dados.getCategoria());
            transacao.setDescricao(dados.getDescricao());
            transacao.setDataTransacao(dados.getDataTransacao());
            return transacaoRepository.save(transacao);
        }

        transacao.setCategoria(dados.getCategoria());
        transacao.setIdPessoa(dados.getIdPessoa());
        transacao.setIdConta(dados.getIdConta());
        transacao.setDescricao(dados.getDescricao());
        transacao.setValor(dados.getValor());
        transacao.setDataTransacao(dados.getDataTransacao());
        transacao.setTipo(dados.getTipo());
        transacao.setPaga(dados.getPaga());

        Transacao salva = transacaoRepository.save(transacao);
        if (efetivada(salva)) {
            movimentarConta(salva, false);
        }
        return salva;
    }

    @Transactional
    public void remover(String id) {
        Transacao transacao = buscarPorId(id);
        // Remover uma transacao efetivada devolve o valor ao saldo da conta
        if (efetivada(transacao)) {
            movimentarConta(transacao, true);
        }
        transacaoRepository.delete(transacao);
    }

    private void validarEPreparar(Transacao transacao) {
        usuarioService.buscarPorId(transacao.getIdPessoa()); // 404 se a pessoa nao existir
        Categoria categoria = categoriaService.buscarPorId(
                transacao.getCategoria() == null ? null : transacao.getCategoria().getId());
        transacao.setCategoria(categoria);

        // Regra: a categoria precisa ser do mesmo tipo da transacao
        if (categoria.getTipoPadrao() != transacao.getTipo()) {
            throw new TransacaoException("A categoria " + categoria.getNome() + " é de "
                    + categoria.getTipoPadrao() + " e não aceita transação do tipo " + transacao.getTipo());
        }
        // Regra: "paga" so existe em despesa (receita fica null; despesa sem informar fica pendente)
        if (transacao.getTipo() == TipoTransacao.RECEITA) {
            if (transacao.getPaga() != null) {
                throw new TransacaoException("O campo paga só se aplica a despesas");
            }
        } else if (transacao.getPaga() == null) {
            transacao.setPaga(false);
        }
        // Regra: despesa paga nao pode ter data futura
        if (Boolean.TRUE.equals(transacao.getPaga()) && transacao.getDataTransacao().isAfter(LocalDate.now())) {
            throw new TransacaoException("Uma despesa paga não pode ter data futura");
        }
        // Regra: a conta precisa existir (404) e pertencer a mesma pessoa
        if (transacao.getIdConta() != null) {
            ContaBancaria conta = contaBancariaService.buscarPorId(transacao.getIdConta());
            if (!conta.getIdUsuario().equals(transacao.getIdPessoa())) {
                throw new TransacaoException("A conta " + conta.getId() + " não pertence à pessoa informada");
            }
        }
    }

    private boolean alteraCamposDoSaldo(Transacao atual, Transacao dados) {
        return !atual.getIdPessoa().equals(dados.getIdPessoa())
                || !Objects.equals(atual.getIdConta(), dados.getIdConta())
                || atual.getValor().compareTo(dados.getValor()) != 0
                || atual.getTipo() != dados.getTipo()
                || !Objects.equals(atual.getPaga(), dados.getPaga());
    }

    // Receita e despesa paga movimentam o saldo
    private boolean efetivada(Transacao transacao) {
        return transacao.getTipo() == TipoTransacao.RECEITA || Boolean.TRUE.equals(transacao.getPaga());
    }

    private void movimentarConta(Transacao transacao, boolean estorno) {
        if (transacao.getIdConta() == null) {
            return;
        }
        boolean credito = (transacao.getTipo() == TipoTransacao.RECEITA) != estorno;
        try {
            if (credito) {
                contaBancariaService.creditar(transacao.getIdConta(), transacao.getValor());
            } else {
                contaBancariaService.debitar(transacao.getIdConta(), transacao.getValor());
            }
        } catch (ContaException e) {
            throw new TransacaoException("Não foi possível movimentar a conta: " + e.getMessage(), e);
        }
    }
}
