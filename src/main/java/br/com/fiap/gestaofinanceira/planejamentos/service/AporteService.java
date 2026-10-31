package br.com.fiap.gestaofinanceira.planejamentos.service;

import br.com.fiap.gestaofinanceira.compartilhado.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.planejamentos.entity.Aporte;
import br.com.fiap.gestaofinanceira.planejamentos.entity.MetaFinanceira;
import br.com.fiap.gestaofinanceira.planejamentos.exception.PlanejamentoException;
import br.com.fiap.gestaofinanceira.planejamentos.repository.AporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AporteService {

    private final AporteRepository aporteRepository;
    private final MetaFinanceiraService metaService;

    public AporteService(AporteRepository aporteRepository, MetaFinanceiraService metaService) {
        this.aporteRepository = aporteRepository;
        this.metaService = metaService;
    }

    @Transactional
    public Aporte cadastrar(Aporte aporte) {
        MetaFinanceira meta = buscarMetaDoAporte(aporte);

        // regra de negócio: meta já concluída não recebe novos aportes
        if (metaService.estaConcluida(meta)) {
            throw new PlanejamentoException(
                    "A meta '" + meta.getNome() + "' já foi concluída e não aceita novos aportes");
        }
        validarDataAporte(aporte);

        aporte.setId(null);
        aporte.setMeta(meta);
        return aporteRepository.save(aporte);
    }

    public List<Aporte> listarTodos() {
        return aporteRepository.findAll();
    }

    public Aporte buscarPorId(String id) {
        return aporteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aporte não encontrado para o id " + id));
    }

    public List<Aporte> listarPorMeta(String idMeta) {
        metaService.buscarPorId(idMeta);
        return aporteRepository.findByMetaId(idMeta);
    }

    @Transactional
    public Aporte atualizar(String id, Aporte dados) {
        Aporte existente = buscarPorId(id);
        MetaFinanceira meta = buscarMetaDoAporte(dados);
        validarDataAporte(dados);

        existente.setMeta(meta);
        existente.setValor(dados.getValor());
        existente.setDataAporte(dados.getDataAporte());
        return aporteRepository.save(existente);
    }

    @Transactional
    public void remover(String id) {
        Aporte aporte = buscarPorId(id);
        aporteRepository.delete(aporte);
    }

    private MetaFinanceira buscarMetaDoAporte(Aporte aporte) {
        if (aporte.getMeta() == null || aporte.getMeta().getId() == null
                || aporte.getMeta().getId().isBlank()) {
            throw new PlanejamentoException("Informe o id da meta que receberá o aporte");
        }
        return metaService.buscarPorId(aporte.getMeta().getId());
    }

    // regra de negócio: o aporte não pode ter data futura
    private void validarDataAporte(Aporte aporte) {
        if (aporte.getDataAporte().isAfter(LocalDate.now())) {
            throw new PlanejamentoException("A data do aporte não pode ser futura");
        }
    }
}