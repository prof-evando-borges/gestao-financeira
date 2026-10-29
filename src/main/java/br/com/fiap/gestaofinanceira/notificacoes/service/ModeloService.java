package br.com.fiap.gestaofinanceira.notificacoes.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Modelo;
import br.com.fiap.gestaofinanceira.notificacoes.entity.TipoEvento;
import br.com.fiap.gestaofinanceira.notificacoes.exception.NotificacaoException;
import br.com.fiap.gestaofinanceira.notificacoes.repository.ModeloRepository;
import br.com.fiap.gestaofinanceira.notificacoes.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModeloService {

    private final ModeloRepository modeloRepository;
    private final NotificacaoRepository notificacaoRepository;

    public ModeloService(ModeloRepository modeloRepository, NotificacaoRepository notificacaoRepository) {
        this.modeloRepository = modeloRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional
    public Modelo cadastrar(Modelo modelo) {
        // Regra 1: o codigo do modelo e unico
        if (modeloRepository.existsByCodigo(modelo.getCodigo())) {
            throw new NotificacaoException("Já existe um modelo com o código " + modelo.getCodigo());
        }
        modelo.setId(null); // o id e sempre gerado pela aplicacao
        modelo.setCriadoEm(null);
        return modeloRepository.save(modelo);
    }

    public List<Modelo> listarTodos() {
        return modeloRepository.findAll();
    }

    public List<Modelo> listarPorTipoEvento(TipoEvento tipoEvento) {
        return modeloRepository.findByTipoEvento(tipoEvento);
    }

    /**
     * Contrato com os outros módulos: lança 404 se o modelo não existir.
     */
    public Modelo buscarPorId(String id) {
        if (id == null) {
            throw new RecursoNaoEncontradoException("Informe o id do modelo");
        }
        return modeloRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Modelo não encontrado com o id " + id));
    }

    @Transactional
    public Modelo atualizar(String id, Modelo dados) {
        Modelo modelo = buscarPorId(id);

        // Regra 1 na atualizacao: o novo codigo nao pode ser de outro modelo
        if (modeloRepository.existsByCodigoAndIdNot(dados.getCodigo(), id)) {
            throw new NotificacaoException("O código " + dados.getCodigo() + " já pertence a outro modelo");
        }

        // Regra 2: modelo ja usado em notificacoes nao muda de tipo de evento
        if (modelo.getTipoEvento() != dados.getTipoEvento() && notificacaoRepository.existsByModeloId(id)) {
            throw new NotificacaoException("O modelo " + modelo.getCodigo() + " já foi usado em notificações do evento "
                    + modelo.getTipoEvento() + " e não pode mudar para " + dados.getTipoEvento()
                    + ". Cadastre um novo modelo");
        }

        modelo.setCodigo(dados.getCodigo());
        modelo.setTitulo(dados.getTitulo());
        modelo.setConteudo(dados.getConteudo());
        modelo.setTipoEvento(dados.getTipoEvento());
        modelo.setAtivo(dados.getAtivo());
        return modeloRepository.save(modelo);
    }

    @Transactional
    public void remover(String id) {
        Modelo modelo = buscarPorId(id);

        // Regra 3: modelo ja usado em notificacoes nao pode ser removido; desative o modelo
        if (notificacaoRepository.existsByModeloId(id)) {
            throw new NotificacaoException("O modelo " + modelo.getCodigo() + " já foi usado em notificações "
                    + "e não pode ser removido. Desative o modelo (ativo = false)");
        }
        modeloRepository.delete(modelo);
    }
}
