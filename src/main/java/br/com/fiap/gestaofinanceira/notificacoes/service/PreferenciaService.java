package br.com.fiap.gestaofinanceira.notificacoes.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Canal;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Preferencia;
import br.com.fiap.gestaofinanceira.notificacoes.entity.TipoEvento;
import br.com.fiap.gestaofinanceira.notificacoes.exception.NotificacaoException;
import br.com.fiap.gestaofinanceira.notificacoes.repository.PreferenciaRepository;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PreferenciaService {

    private final PreferenciaRepository preferenciaRepository;
    private final CanalService canalService;
    // Modulo 01 (Pessoas): a pessoa e conferida pelo Service do grupo dono, nunca pelo Repository dele
    private final UsuarioService usuarioService;

    public PreferenciaService(PreferenciaRepository preferenciaRepository,
                              CanalService canalService,
                              UsuarioService usuarioService) {
        this.preferenciaRepository = preferenciaRepository;
        this.canalService = canalService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Preferencia cadastrar(Preferencia preferencia) {
        usuarioService.buscarPorId(preferencia.getIdPessoa()); // 404 se a pessoa nao existir
        Canal canal = canalService.buscarPorId(preferencia.getCanal().getId()); // 404 se o canal nao existir

        // Regra 1: uma preferencia por pessoa, canal e tipo de evento
        if (preferenciaRepository.existsByIdPessoaAndCanalIdAndTipoEvento(
                preferencia.getIdPessoa(), canal.getId(), preferencia.getTipoEvento())) {
            throw new NotificacaoException("Esta pessoa já tem uma preferência para o evento "
                    + preferencia.getTipoEvento() + " no canal " + canal.getNome()
                    + ". Atualize a preferência existente");
        }
        validarCanalAtivo(canal, preferencia.getHabilitado());

        preferencia.setId(null); // o id e sempre gerado pela aplicacao
        preferencia.setCanal(canal);
        return preferenciaRepository.save(preferencia);
    }

    public List<Preferencia> listarTodos() {
        return preferenciaRepository.findAll();
    }

    public List<Preferencia> listarPorPessoa(String idPessoa) {
        usuarioService.buscarPorId(idPessoa); // 404 se a pessoa nao existir
        return preferenciaRepository.findByIdPessoa(idPessoa);
    }

    /**
     * Contrato com os outros módulos: lança 404 se a preferência não existir.
     */
    public Preferencia buscarPorId(String id) {
        return preferenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Preferência não encontrada com o id " + id));
    }

    /**
     * Usado pelo NotificacaoService para saber se a pessoa aceita o evento naquele canal.
     */
    public Optional<Preferencia> buscarPreferencia(String idPessoa, String idCanal, TipoEvento tipoEvento) {
        return preferenciaRepository.findByIdPessoaAndCanalIdAndTipoEvento(idPessoa, idCanal, tipoEvento);
    }

    @Transactional
    public Preferencia atualizar(String id, Preferencia dados) {
        Preferencia preferencia = buscarPorId(id);

        // Regra 3: a pessoa identifica a preferencia e nao muda
        if (!preferencia.getIdPessoa().equals(dados.getIdPessoa())) {
            throw new NotificacaoException("Não é possível trocar a pessoa de uma preferência. "
                    + "Cadastre uma nova preferência para a outra pessoa");
        }
        Canal canal = canalService.buscarPorId(dados.getCanal().getId()); // 404 se o canal nao existir

        // Regra 1 na atualizacao: a combinacao nao pode repetir a de outra preferencia
        if (preferenciaRepository.existsByIdPessoaAndCanalIdAndTipoEventoAndIdNot(
                preferencia.getIdPessoa(), canal.getId(), dados.getTipoEvento(), id)) {
            throw new NotificacaoException("Esta pessoa já tem outra preferência para o evento "
                    + dados.getTipoEvento() + " no canal " + canal.getNome());
        }
        validarCanalAtivo(canal, dados.getHabilitado());

        preferencia.setCanal(canal);
        preferencia.setTipoEvento(dados.getTipoEvento());
        preferencia.setHabilitado(dados.getHabilitado());
        preferencia.setAntecedenciaDias(dados.getAntecedenciaDias());
        return preferenciaRepository.save(preferencia);
    }

    @Transactional
    public void remover(String id) {
        Preferencia preferencia = buscarPorId(id);
        preferenciaRepository.delete(preferencia);
    }

    // Regra 2: nao da para habilitar o recebimento por um canal desativado
    private void validarCanalAtivo(Canal canal, Boolean habilitado) {
        if (Boolean.TRUE.equals(habilitado) && !Boolean.TRUE.equals(canal.getAtivo())) {
            throw new NotificacaoException("O canal " + canal.getNome() + " está desativado; "
                    + "a preferência só pode ser cadastrada com habilitado = false");
        }
    }
}
