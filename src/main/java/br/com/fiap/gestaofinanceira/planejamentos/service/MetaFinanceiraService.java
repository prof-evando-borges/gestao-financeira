package br.com.fiap.gestaofinanceira.planejamentos.service;

import br.com.fiap.gestaofinanceira.compartilhado.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import br.com.fiap.gestaofinanceira.planejamentos.entity.Aporte;
import br.com.fiap.gestaofinanceira.planejamentos.entity.MetaFinanceira;
import br.com.fiap.gestaofinanceira.planejamentos.exception.PlanejamentoException;
import br.com.fiap.gestaofinanceira.planejamentos.repository.AporteRepository;
import br.com.fiap.gestaofinanceira.planejamentos.repository.MetaFinanceiraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MetaFinanceiraService {

    private final MetaFinanceiraRepository metaRepository;
    private final AporteRepository aporteRepository;
    private final UsuarioService usuarioService;

    public MetaFinanceiraService(MetaFinanceiraRepository metaRepository,
                                 AporteRepository aporteRepository,
                                 UsuarioService usuarioService) {
        this.metaRepository = metaRepository;
        this.aporteRepository = aporteRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public MetaFinanceira cadastrar(MetaFinanceira meta) {
        // integração: confere se o usuário existe no módulo dono (404 se não existir)
        usuarioService.buscarPorId(meta.getIdUsuario());
        validarDatas(meta);
        meta.setId(null);
        return metaRepository.save(meta);
    }

    public List<MetaFinanceira> listarTodos() {
        return metaRepository.findAll();
    }

    public MetaFinanceira buscarPorId(String id) {
        return metaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Meta financeira não encontrada para o id " + id));
    }

    public List<MetaFinanceira> listarPorUsuario(String idUsuario) {
        usuarioService.buscarPorId(idUsuario);
        return metaRepository.findByIdUsuario(idUsuario);
    }

    public List<MetaFinanceira> listarAtrasadas() {
        return metaRepository.findByDataLimiteBefore(LocalDate.now()).stream()
                .filter(meta -> !estaConcluida(meta))
                .toList();
    }

    @Transactional
    public MetaFinanceira atualizar(String id, MetaFinanceira dados) {
        MetaFinanceira existente = buscarPorId(id);
        usuarioService.buscarPorId(dados.getIdUsuario());
        validarDatas(dados);

        existente.setIdUsuario(dados.getIdUsuario());
        existente.setNome(dados.getNome());
        existente.setValorObjetivo(dados.getValorObjetivo());
        existente.setDataCriacao(dados.getDataCriacao());
        existente.setDataLimite(dados.getDataLimite());
        return metaRepository.save(existente);
    }

    @Transactional
    public void remover(String id) {
        MetaFinanceira meta = buscarPorId(id);
        // regra de negócio: não remover meta que já recebeu aportes
        if (!aporteRepository.findByMetaId(id).isEmpty()) {
            throw new PlanejamentoException(
                    "Não é possível remover a meta '" + meta.getNome() + "' porque ela já possui aportes");
        }
        metaRepository.delete(meta);
    }

    public BigDecimal calcularTotalAportado(String idMeta) {
        return aporteRepository.findByMetaId(idMeta).stream()
                .map(Aporte::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularValorRestante(MetaFinanceira meta) {
        BigDecimal restante = meta.getValorObjetivo().subtract(calcularTotalAportado(meta.getId()));
        return restante.max(BigDecimal.ZERO);
    }

    public boolean estaConcluida(MetaFinanceira meta) {
        return calcularTotalAportado(meta.getId()).compareTo(meta.getValorObjetivo()) >= 0;
    }

    // regra de negócio: a data limite não pode ser anterior à data de criação
    private void validarDatas(MetaFinanceira meta) {
        if (meta.getDataLimite().isBefore(meta.getDataCriacao())) {
            throw new PlanejamentoException(
                    "A data limite não pode ser anterior à data de criação da meta");
        }
    }
}