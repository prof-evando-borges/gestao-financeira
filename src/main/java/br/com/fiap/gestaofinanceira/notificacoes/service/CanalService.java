package br.com.fiap.gestaofinanceira.notificacoes.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Canal;
import br.com.fiap.gestaofinanceira.notificacoes.exception.NotificacaoException;
import br.com.fiap.gestaofinanceira.notificacoes.repository.CanalRepository;
import br.com.fiap.gestaofinanceira.notificacoes.repository.NotificacaoRepository;
import br.com.fiap.gestaofinanceira.notificacoes.repository.PreferenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CanalService {

    private final CanalRepository canalRepository;
    private final PreferenciaRepository preferenciaRepository;
    private final NotificacaoRepository notificacaoRepository;

    public CanalService(CanalRepository canalRepository,
                        PreferenciaRepository preferenciaRepository,
                        NotificacaoRepository notificacaoRepository) {
        this.canalRepository = canalRepository;
        this.preferenciaRepository = preferenciaRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional
    public Canal cadastrar(Canal canal) {
        // Regra 1: cada canal (EMAIL, SMS, PUSH, APP) existe uma unica vez
        if (canalRepository.existsByNome(canal.getNome())) {
            throw new NotificacaoException("O canal " + canal.getNome() + " já está cadastrado");
        }
        canal.setId(null); // o id e sempre gerado pela aplicacao
        return canalRepository.save(canal);
    }

    public List<Canal> listarTodos() {
        return canalRepository.findAll();
    }

    public List<Canal> listarPorAtivo(Boolean ativo) {
        return canalRepository.findByAtivo(ativo);
    }

    /**
     * Contrato com os outros módulos: lança 404 se o canal não existir.
     */
    public Canal buscarPorId(String id) {
        if (id == null) {
            throw new RecursoNaoEncontradoException("Informe o id do canal");
        }
        return canalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Canal não encontrado com o id " + id));
    }

    @Transactional
    public Canal atualizar(String id, Canal dados) {
        Canal canal = buscarPorId(id);

        // Regra 1 na atualizacao: o novo nome nao pode ser de outro canal
        if (canalRepository.existsByNomeAndIdNot(dados.getNome(), id)) {
            throw new NotificacaoException("O canal " + dados.getNome() + " já está cadastrado com outro id");
        }

        canal.setNome(dados.getNome());
        canal.setDescricao(dados.getDescricao());
        canal.setAtivo(dados.getAtivo());
        return canalRepository.save(canal);
    }

    @Transactional
    public void remover(String id) {
        Canal canal = buscarPorId(id);

        // Regra 2: canal em uso nao pode ser removido; desative o canal (ativo = false)
        if (preferenciaRepository.existsByCanalId(id) || notificacaoRepository.existsByCanalId(id)) {
            throw new NotificacaoException("O canal " + canal.getNome() + " possui preferências ou notificações "
                    + "vinculadas e não pode ser removido. Desative o canal (ativo = false)");
        }
        canalRepository.delete(canal);
    }
}
