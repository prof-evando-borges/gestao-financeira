package br.com.fiap.gestaofinanceira.investimentos.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.investimentos.dto.CarteiraInvestidor;
import br.com.fiap.gestaofinanceira.investimentos.entity.Aplicacao;
import br.com.fiap.gestaofinanceira.investimentos.entity.Investidor;
import br.com.fiap.gestaofinanceira.investimentos.entity.PerfilInvestidor;
import br.com.fiap.gestaofinanceira.investimentos.entity.StatusAplicacao;
import br.com.fiap.gestaofinanceira.investimentos.entity.TipoInvestimento;
import br.com.fiap.gestaofinanceira.investimentos.exception.InvestimentoException;
import br.com.fiap.gestaofinanceira.investimentos.repository.AplicacaoRepository;
import br.com.fiap.gestaofinanceira.investimentos.repository.InvestidorRepository;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvestidorService {

    private final InvestidorRepository investidorRepository;
    private final AplicacaoRepository aplicacaoRepository;
    private final UsuarioService usuarioService;

    public InvestidorService(InvestidorRepository investidorRepository,
                             AplicacaoRepository aplicacaoRepository,
                             UsuarioService usuarioService) {
        this.investidorRepository = investidorRepository;
        this.aplicacaoRepository = aplicacaoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Investidor cadastrar(Investidor investidor) {
        // Integracao com o modulo 01: 404 se a pessoa nao existir
        usuarioService.buscarPorId(investidor.getIdPessoa());

        // Regra: uma pessoa tem no maximo um perfil de investidor
        if (investidorRepository.existsByIdPessoa(investidor.getIdPessoa())) {
            throw new InvestimentoException(
                    "Esta pessoa já tem um perfil de investidor. Atualize o perfil existente em vez de cadastrar outro.");
        }

        investidor.setId(null);
        return investidorRepository.save(investidor);
    }

    public List<Investidor> listarTodos() {
        return investidorRepository.findAll();
    }

    public List<Investidor> listarPorPerfil(PerfilInvestidor perfil) {
        return investidorRepository.findByPerfil(perfil);
    }

    public Investidor buscarPorId(String id) {
        return investidorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Investidor não encontrado: " + id));
    }

    @Transactional
    public Investidor atualizar(String id, Investidor dados) {
        Investidor existente = buscarPorId(id);

        // Regra: o perfil pertence a uma pessoa; trocar a pessoa seria outro perfil
        if (!existente.getIdPessoa().equals(dados.getIdPessoa())) {
            throw new InvestimentoException(
                    "Não é possível trocar a pessoa de um perfil de investidor. Cadastre um novo perfil para a outra pessoa.");
        }

        // Regra: o novo perfil precisa aceitar o risco das aplicacoes ativas
        if (dados.getPerfil() != existente.getPerfil()) {
            validarAplicacoesAtivasNoPerfil(existente.getId(), dados.getPerfil());
        }

        existente.setPerfil(dados.getPerfil());
        existente.setObjetivo(dados.getObjetivo());
        return investidorRepository.save(existente);
    }

    @Transactional
    public void remover(String id) {
        Investidor existente = buscarPorId(id);

        // Regra: nao remover investidor com dinheiro aplicado; aplicacoes ja resgatadas saem junto (cascade)
        if (aplicacaoRepository.existsByInvestidorIdAndStatus(id, StatusAplicacao.ATIVA)) {
            throw new InvestimentoException(
                    "O investidor tem aplicações ativas e não pode ser removido. Resgate ou remova as aplicações antes.");
        }

        investidorRepository.delete(existente);
    }

    public CarteiraInvestidor consultarCarteira(String id) {
        Investidor investidor = buscarPorId(id);
        List<Aplicacao> ativas = aplicacaoRepository.findByInvestidorIdAndStatus(id, StatusAplicacao.ATIVA);

        BigDecimal totalAplicado = ativas.stream()
                .map(Aplicacao::getValorAplicado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<TipoInvestimento, BigDecimal> totalPorTipo = ativas.stream()
                .collect(Collectors.groupingBy(
                        Aplicacao::getTipo,
                        () -> new EnumMap<>(TipoInvestimento.class),
                        Collectors.reducing(BigDecimal.ZERO, Aplicacao::getValorAplicado, BigDecimal::add)));

        BigDecimal montante12Meses = ativas.stream()
                .map(aplicacao -> aplicacao.projetarMontante(12))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarteiraInvestidor(investidor.getId(), investidor.getPerfil(), ativas.size(),
                totalAplicado, totalPorTipo, montante12Meses);
    }

    private void validarAplicacoesAtivasNoPerfil(String idInvestidor, PerfilInvestidor novoPerfil) {
        List<TipoInvestimento> incompativeis = aplicacaoRepository
                .findByInvestidorIdAndStatus(idInvestidor, StatusAplicacao.ATIVA).stream()
                .map(Aplicacao::getTipo)
                .filter(tipo -> !novoPerfil.permite(tipo))
                .distinct()
                .toList();

        if (!incompativeis.isEmpty()) {
            throw new InvestimentoException("O perfil " + novoPerfil + " aceita risco até "
                    + novoPerfil.getRiscoMaximo() + ", mas o investidor tem aplicações ativas em " + incompativeis
                    + ". Resgate essas aplicações antes de mudar o perfil.");
        }
    }
}
