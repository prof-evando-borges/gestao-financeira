package br.com.fiap.gestaofinanceira.transacoes.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.transacoes.entity.Categoria;
import br.com.fiap.gestaofinanceira.transacoes.exception.TransacaoException;
import br.com.fiap.gestaofinanceira.transacoes.repository.CategoriaRepository;
import br.com.fiap.gestaofinanceira.transacoes.repository.TransacaoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            TransacaoRepository transacaoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional
    public Categoria cadastrar(Categoria categoria) {
        // Regra: nao cadastrar categoria com nome repetido
        if (categoriaRepository.findByNome(categoria.getNome()).isPresent()) {
            throw new TransacaoException("Já existe uma categoria com o nome " + categoria.getNome());
        }
        categoria.setId(null); // o id e sempre gerado pela aplicacao
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodos() {
        return categoriaRepository.findAll();
    }

    /**
     * Contrato com os outros modulos: lanca 404 se a categoria nao existir.
     */
    public Categoria buscarPorId(String id) {
        if (id == null) {
            throw new RecursoNaoEncontradoException("Informe o id da categoria");
        }
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com o id " + id));
    }

    public Categoria buscarPorNome(String nome) {
        return categoriaRepository.findByNome(nome)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com o nome " + nome));
    }

    @Transactional
    public Categoria atualizar(String id, Categoria dados) {
        Categoria categoria = buscarPorId(id);

        if (categoriaRepository.existsByNomeAndIdNot(dados.getNome(), id)) {
            throw new TransacaoException("O nome " + dados.getNome() + " já pertence a outra categoria");
        }
        // Regra: com transacoes lancadas, o tipo padrao nao pode mudar
        if (categoria.getTipoPadrao() != dados.getTipoPadrao() && transacaoRepository.existsByCategoriaId(id)) {
            throw new TransacaoException("Não é possível mudar o tipo padrão de uma categoria que já possui transações");
        }

        categoria.setNome(dados.getNome());
        categoria.setTipoPadrao(dados.getTipoPadrao());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void remover(String id) {
        Categoria categoria = buscarPorId(id);

        // Regra: nao remover categoria que ainda tem transacoes
        if (transacaoRepository.existsByCategoriaId(id)) {
            throw new TransacaoException("A categoria possui transações lançadas. Remova as transações antes de remover a categoria");
        }
        try {
            categoriaRepository.delete(categoria);
            categoriaRepository.flush(); // forca o DELETE agora para cair no catch
        } catch (DataIntegrityViolationException e) {
            throw new TransacaoException("A categoria não pode ser removida porque está vinculada a outros registros", e);
        }
    }
}
