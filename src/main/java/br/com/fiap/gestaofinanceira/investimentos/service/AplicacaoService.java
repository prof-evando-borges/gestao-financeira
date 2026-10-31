package br.com.fiap.gestaofinanceira.investimentos.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.contas.entity.ContaBancaria;
import br.com.fiap.gestaofinanceira.contas.entity.StatusConta;
import br.com.fiap.gestaofinanceira.contas.service.ContaBancariaService;
import br.com.fiap.gestaofinanceira.investimentos.dto.ProjecaoRendimento;
import br.com.fiap.gestaofinanceira.investimentos.entity.Aplicacao;
import br.com.fiap.gestaofinanceira.investimentos.entity.Investidor;
import br.com.fiap.gestaofinanceira.investimentos.entity.StatusAplicacao;
import br.com.fiap.gestaofinanceira.investimentos.entity.TipoInvestimento;
import br.com.fiap.gestaofinanceira.investimentos.exception.InvestimentoException;
import br.com.fiap.gestaofinanceira.investimentos.repository.AplicacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AplicacaoService {

    private final AplicacaoRepository aplicacaoRepository;
    private final InvestidorService investidorService;
    private final ContaBancariaService contaBancariaService;

    public AplicacaoService(AplicacaoRepository aplicacaoRepository,
                            InvestidorService investidorService,
                            ContaBancariaService contaBancariaService) {
        this.aplicacaoRepository = aplicacaoRepository;
        this.investidorService = investidorService;
        this.contaBancariaService = contaBancariaService;
    }

    @Transactional
    public Aplicacao cadastrar(Aplicacao aplicacao) {
        Investidor investidor = investidorService.buscarPorId(aplicacao.getInvestidor().getId());
        validarRegras(aplicacao, investidor);

        aplicacao.setId(null);
        aplicacao.setInvestidor(investidor);
        aplicacao.setStatus(StatusAplicacao.ATIVA);
        return aplicacaoRepository.save(aplicacao);
    }

    public List<Aplicacao> listarTodos() {
        return aplicacaoRepository.findAll();
    }

    /**
     * Consulta com filtros opcionais: por investidor, por tipo ou pelos dois.
     */
    public List<Aplicacao> listar(String idInvestidor, TipoInvestimento tipo) {
        if (idInvestidor != null) {
            investidorService.buscarPorId(idInvestidor); // 404 se o investidor nao existir
            List<Aplicacao> doInvestidor = aplicacaoRepository.findByInvestidorId(idInvestidor);
            return tipo == null ? doInvestidor
                    : doInvestidor.stream().filter(aplicacao -> aplicacao.getTipo() == tipo).toList();
        }
        if (tipo != null) {
            return aplicacaoRepository.findByTipo(tipo);
        }
        return listarTodos();
    }

    public Aplicacao buscarPorId(String id) {
        return aplicacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aplicação não encontrada: " + id));
    }

    @Transactional
    public Aplicacao atualizar(String id, Aplicacao dados) {
        Aplicacao existente = buscarPorId(id);

        // Regra: aplicacao resgatada faz parte do historico e nao muda mais
        if (existente.getStatus() == StatusAplicacao.RESGATADA) {
            throw new InvestimentoException("Esta aplicação já foi resgatada e não pode mais ser alterada.");
        }

        Investidor investidor = investidorService.buscarPorId(dados.getInvestidor().getId());
        validarRegras(dados, investidor);

        existente.setInvestidor(investidor);
        existente.setIdConta(dados.getIdConta());
        existente.setDescricao(dados.getDescricao());
        existente.setTipo(dados.getTipo());
        existente.setValorAplicado(dados.getValorAplicado());
        existente.setTaxaAnual(dados.getTaxaAnual());
        existente.setDataAplicacao(dados.getDataAplicacao());
        existente.setDataVencimento(dados.getDataVencimento());
        if (dados.getStatus() != null) {
            existente.setStatus(dados.getStatus()); // RESGATADA = resgate da aplicacao
        }
        return aplicacaoRepository.save(existente);
    }

    @Transactional
    public void remover(String id) {
        Aplicacao existente = buscarPorId(id);
        aplicacaoRepository.delete(existente);
    }

    public ProjecaoRendimento projetarRendimento(String id, int meses) {
        Aplicacao aplicacao = buscarPorId(id);
        BigDecimal montante = aplicacao.projetarMontante(meses);
        return new ProjecaoRendimento(aplicacao.getId(), aplicacao.getValorAplicado(), aplicacao.getTaxaAnual(),
                meses, montante, montante.subtract(aplicacao.getValorAplicado()));
    }

    private void validarRegras(Aplicacao aplicacao, Investidor investidor) {
        // Integracao com o modulo 02: 404 se a conta nao existir
        ContaBancaria conta = contaBancariaService.buscarPorId(aplicacao.getIdConta());

        // Regra: o dinheiro sai de uma conta ativa do proprio investidor
        if (!conta.getIdUsuario().equals(investidor.getIdPessoa())) {
            throw new InvestimentoException("A conta de origem precisa ser da mesma pessoa do investidor.");
        }
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new InvestimentoException("A conta de origem está " + conta.getStatus()
                    + " e não pode ser usada em aplicações.");
        }

        // Regra (suitability): o tipo precisa caber no risco aceito pelo perfil
        if (!investidor.getPerfil().permite(aplicacao.getTipo())) {
            throw new InvestimentoException("O perfil " + investidor.getPerfil() + " aceita risco até "
                    + investidor.getPerfil().getRiscoMaximo() + ", e " + aplicacao.getTipo() + " tem risco "
                    + aplicacao.getTipo().getRisco() + ".");
        }

        // Regra: vencimento, quando houver, depois da data da aplicacao
        if (aplicacao.getDataVencimento() != null
                && !aplicacao.getDataVencimento().isAfter(aplicacao.getDataAplicacao())) {
            throw new InvestimentoException("A data de vencimento deve ser posterior à data da aplicação.");
        }
    }
}
