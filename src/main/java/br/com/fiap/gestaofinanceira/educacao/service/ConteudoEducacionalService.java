package br.com.fiap.gestaofinanceira.educacao.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.educacao.entity.ConteudoEducacional;
import br.com.fiap.gestaofinanceira.educacao.entity.NivelConhecimento;
import br.com.fiap.gestaofinanceira.educacao.exception.EducacaoException;
import br.com.fiap.gestaofinanceira.educacao.repository.ConteudoEducacionalRepository;
import br.com.fiap.gestaofinanceira.educacao.repository.ProgressoUsuarioRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConteudoEducacionalService {

    private final ConteudoEducacionalRepository conteudoRepository;
    private final ProgressoUsuarioRepository progressoRepository;

    public ConteudoEducacionalService(ConteudoEducacionalRepository conteudoRepository,
                                      ProgressoUsuarioRepository progressoRepository) {
        this.conteudoRepository = conteudoRepository;
        this.progressoRepository = progressoRepository;
    }

    @Transactional
    public ConteudoEducacional cadastrar(ConteudoEducacional conteudo) {
        // Regra 1: o titulo nao pode repetir
        if (conteudoRepository.existsByTituloIgnoreCase(conteudo.getTitulo())) {
            throw new EducacaoException("Já existe um conteúdo com o título \"" + conteudo.getTitulo() + "\"");
        }

        conteudo.setId(null); // o id e sempre gerado pela aplicacao
        if (conteudo.getAtivo() == null) {
            conteudo.setAtivo(Boolean.TRUE);
        }
        return conteudoRepository.save(conteudo);
    }

    public List<ConteudoEducacional> listarTodos() {
        return conteudoRepository.findAll();
    }

    /**
     * Contrato com os outros módulos: lança 404 se o conteúdo não existir.
     */
    public ConteudoEducacional buscarPorId(String id) {
        return conteudoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conteúdo educacional não encontrado com o id " + id));
    }

    public List<ConteudoEducacional> listarPorNivel(NivelConhecimento nivel) {
        return conteudoRepository.findByNivel(nivel);
    }

    @Transactional
    public ConteudoEducacional atualizar(String id, ConteudoEducacional dados) {
        ConteudoEducacional conteudo = buscarPorId(id);

        // Regra 2: o tipo (ARTIGO, VIDEO ou CURSO) nao muda depois do cadastro
        if (!Hibernate.getClass(conteudo).equals(dados.getClass())) {
            throw new EducacaoException("Não é possível trocar o tipo de um conteúdo já cadastrado. "
                    + "Cadastre um novo conteúdo com o tipo desejado");
        }

        // Regra 1 na atualizacao: o novo titulo nao pode ser de outro conteudo
        if (conteudoRepository.existsByTituloIgnoreCaseAndIdNot(dados.getTitulo(), id)) {
            throw new EducacaoException("Já existe outro conteúdo com o título \"" + dados.getTitulo() + "\"");
        }

        conteudo.setTitulo(dados.getTitulo());
        conteudo.setDescricao(dados.getDescricao());
        conteudo.setNivel(dados.getNivel());
        if (dados.getAtivo() != null) {
            conteudo.setAtivo(dados.getAtivo());
        }
        conteudo.atualizarDadosEspecificos(dados); // autor, urlVideo ou numeroModulos
        return conteudoRepository.save(conteudo);
    }

    @Transactional
    public void remover(String id) {
        ConteudoEducacional conteudo = buscarPorId(id);

        // Regra 3: nao remover conteudo que ja tem progresso de algum usuario
        if (progressoRepository.existsByConteudoId(id)) {
            throw new EducacaoException("O conteúdo possui progresso de usuários e não pode ser removido. "
                    + "Desative-o (\"ativo\": false) para que não receba novos progressos");
        }
        conteudoRepository.delete(conteudo);
    }
}
