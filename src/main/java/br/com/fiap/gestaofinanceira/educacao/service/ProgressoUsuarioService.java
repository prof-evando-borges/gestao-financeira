package br.com.fiap.gestaofinanceira.educacao.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.educacao.entity.ConteudoEducacional;
import br.com.fiap.gestaofinanceira.educacao.entity.ProgressoUsuario;
import br.com.fiap.gestaofinanceira.educacao.entity.StatusProgresso;
import br.com.fiap.gestaofinanceira.educacao.exception.EducacaoException;
import br.com.fiap.gestaofinanceira.educacao.repository.ProgressoUsuarioRepository;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgressoUsuarioService {

    private final ProgressoUsuarioRepository progressoRepository;
    private final ConteudoEducacionalService conteudoService;
    private final UsuarioService usuarioService;

    public ProgressoUsuarioService(ProgressoUsuarioRepository progressoRepository,
                                   ConteudoEducacionalService conteudoService,
                                   UsuarioService usuarioService) {
        this.progressoRepository = progressoRepository;
        this.conteudoService = conteudoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public ProgressoUsuario cadastrar(ProgressoUsuario progresso) {
        // Integracao com o modulo 01 (Pessoas): 404 se o usuario nao existir
        usuarioService.buscarPorId(progresso.getIdUsuario());
        // Mesmo modulo: 404 se o conteudo nao existir
        ConteudoEducacional conteudo = conteudoService.buscarPorId(progresso.getIdConteudo());

        // Regra 1: conteudo desativado nao recebe novos progressos
        if (!Boolean.TRUE.equals(conteudo.getAtivo())) {
            throw new EducacaoException("O conteúdo \"" + conteudo.getTitulo()
                    + "\" está desativado e não aceita novos progressos");
        }

        // Regra 2: um usuario tem no maximo um progresso por conteudo
        if (progressoRepository.existsByIdUsuarioAndConteudoId(progresso.getIdUsuario(), conteudo.getId())) {
            throw new EducacaoException("Este usuário já possui progresso no conteúdo \"" + conteudo.getTitulo()
                    + "\". Atualize o progresso existente em vez de cadastrar outro");
        }

        validarPercentual(progresso);
        validarDatas(progresso);

        progresso.setId(null); // o id e sempre gerado pela aplicacao
        progresso.setConteudo(conteudo);
        return progressoRepository.save(progresso);
    }

    public List<ProgressoUsuario> listarTodos() {
        return progressoRepository.findAll();
    }

    /**
     * Contrato com os outros módulos: lança 404 se o progresso não existir.
     */
    public ProgressoUsuario buscarPorId(String id) {
        return progressoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Progresso não encontrado com o id " + id));
    }

    public List<ProgressoUsuario> listarPorUsuario(String idUsuario) {
        usuarioService.buscarPorId(idUsuario); // 404 se o usuario nao existir
        return progressoRepository.findByIdUsuario(idUsuario);
    }

    public List<ProgressoUsuario> listarPorStatus(StatusProgresso status) {
        return progressoRepository.findByStatus(status);
    }

    @Transactional
    public ProgressoUsuario atualizar(String id, ProgressoUsuario dados) {
        ProgressoUsuario progresso = buscarPorId(id);

        // Regra 4: usuario e conteudo identificam o progresso e nao mudam
        if (!progresso.getIdUsuario().equals(dados.getIdUsuario())
                || !progresso.getConteudo().getId().equals(dados.getIdConteudo())) {
            throw new EducacaoException("Não é possível trocar o usuário ou o conteúdo de um progresso. "
                    + "Cadastre um novo progresso para o outro usuário ou conteúdo");
        }

        // Regra 5: progresso concluido nao volta atras
        if (progresso.getStatus() == StatusProgresso.CONCLUIDO && dados.getStatus() != StatusProgresso.CONCLUIDO) {
            throw new EducacaoException("Este conteúdo já foi concluído pelo usuário; o progresso não pode voltar atrás");
        }

        validarPercentual(dados);
        validarDatas(dados);

        progresso.setStatus(dados.getStatus());
        progresso.setPercentual(dados.getPercentual());
        progresso.setDataInicio(dados.getDataInicio());
        progresso.setDataConclusao(dados.getDataConclusao());
        return progressoRepository.save(progresso);
    }

    @Transactional
    public void remover(String id) {
        ProgressoUsuario progresso = buscarPorId(id);
        progressoRepository.delete(progresso);
    }

    // Regra 3 (validarPercentual do UML): o status tem que combinar com o percentual
    // NAO_INICIADO = 0%, EM_ANDAMENTO = 1% a 99%, CONCLUIDO = 100%
    private void validarPercentual(ProgressoUsuario progresso) {
        int percentual = progresso.getPercentual();
        StatusProgresso esperado;
        if (percentual == 0) {
            esperado = StatusProgresso.NAO_INICIADO;
        } else if (percentual == 100) {
            esperado = StatusProgresso.CONCLUIDO;
        } else {
            esperado = StatusProgresso.EM_ANDAMENTO;
        }

        if (progresso.getStatus() != esperado) {
            throw new EducacaoException("Com " + percentual + "% concluído o status deve ser " + esperado
                    + ", e não " + progresso.getStatus());
        }
    }

    // Regra 3 (validarDatas do UML): so tem data de conclusao quem concluiu, e ela nao vem antes do inicio
    private void validarDatas(ProgressoUsuario progresso) {
        boolean concluido = progresso.getStatus() == StatusProgresso.CONCLUIDO;

        if (concluido && progresso.getDataConclusao() == null) {
            throw new EducacaoException("Informe a data de conclusão (dataConclusao) do conteúdo concluído");
        }
        if (!concluido && progresso.getDataConclusao() != null) {
            throw new EducacaoException("Só informe a data de conclusão quando o status for CONCLUIDO");
        }
        if (concluido && progresso.getDataConclusao().isBefore(progresso.getDataInicio())) {
            throw new EducacaoException("A data de conclusão não pode ser anterior à data de início");
        }
    }
}
