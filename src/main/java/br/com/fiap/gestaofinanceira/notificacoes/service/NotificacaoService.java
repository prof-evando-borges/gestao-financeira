package br.com.fiap.gestaofinanceira.notificacoes.service;

import br.com.fiap.gestaofinanceira.compartilhado.excecao.RecursoNaoEncontradoException;
import br.com.fiap.gestaofinanceira.contas.service.ContaBancariaService;
import br.com.fiap.gestaofinanceira.educacao.service.ConteudoEducacionalService;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Canal;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Modelo;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Notificacao;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Preferencia;
import br.com.fiap.gestaofinanceira.notificacoes.entity.StatusNotificacao;
import br.com.fiap.gestaofinanceira.notificacoes.exception.NotificacaoException;
import br.com.fiap.gestaofinanceira.notificacoes.repository.NotificacaoRepository;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Regras das notificações. O status só avança pelo fluxo:
 * PENDENTE -> ENVIADA (envio com sucesso) -> LIDA, PENDENTE -> FALHA (3 envios com falha)
 * e PENDENTE -> CANCELADA.
 */
@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final CanalService canalService;
    private final ModeloService modeloService;
    private final PreferenciaService preferenciaService;
    // Outros modulos: conferidos sempre pelo Service do grupo dono, nunca pelo Repository dele
    private final UsuarioService usuarioService;
    private final ContaBancariaService contaBancariaService;
    private final ConteudoEducacionalService conteudoEducacionalService;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              CanalService canalService,
                              ModeloService modeloService,
                              PreferenciaService preferenciaService,
                              UsuarioService usuarioService,
                              ContaBancariaService contaBancariaService,
                              ConteudoEducacionalService conteudoEducacionalService) {
        this.notificacaoRepository = notificacaoRepository;
        this.canalService = canalService;
        this.modeloService = modeloService;
        this.preferenciaService = preferenciaService;
        this.usuarioService = usuarioService;
        this.contaBancariaService = contaBancariaService;
        this.conteudoEducacionalService = conteudoEducacionalService;
    }

    @Transactional
    public Notificacao cadastrar(Notificacao notificacao) {
        usuarioService.buscarPorId(notificacao.getIdPessoa()); // 404 se a pessoa nao existir
        validarReferencia(notificacao);

        notificacao.setId(null); // o id e sempre gerado pela aplicacao
        notificacao.setStatus(StatusNotificacao.PENDENTE);
        notificacao.setEnviadaEm(null);
        notificacao.setLidaEm(null);
        notificacao.setCriadoEm(null);
        prepararEnvio(notificacao, notificacao);
        return notificacaoRepository.save(notificacao);
    }

    public List<Notificacao> listarTodos() {
        return notificacaoRepository.findAll();
    }

    public List<Notificacao> listar(String idPessoa, StatusNotificacao status) {
        if (idPessoa != null) {
            usuarioService.buscarPorId(idPessoa); // 404 se a pessoa nao existir
            return status != null
                    ? notificacaoRepository.findByIdPessoaAndStatus(idPessoa, status)
                    : notificacaoRepository.findByIdPessoa(idPessoa);
        }
        if (status != null) {
            return notificacaoRepository.findByStatus(status);
        }
        return listarTodos();
    }

    /**
     * Contrato com os outros módulos: lança 404 se a notificação não existir.
     */
    public Notificacao buscarPorId(String id) {
        return notificacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação não encontrada com o id " + id));
    }

    @Transactional
    public Notificacao atualizar(String id, Notificacao dados) {
        Notificacao notificacao = buscarPorId(id);

        // Regra 5: so da para editar o que ainda nao saiu
        if (notificacao.getStatus() != StatusNotificacao.PENDENTE) {
            throw new NotificacaoException("A notificação está " + notificacao.getStatus()
                    + " e não pode mais ser editada; só notificações PENDENTES podem ser alteradas");
        }
        // Regra 6: o destinatario identifica a notificacao e nao muda
        if (!notificacao.getIdPessoa().equals(dados.getIdPessoa())) {
            throw new NotificacaoException("Não é possível trocar a pessoa destinatária de uma notificação. "
                    + "Cadastre uma nova notificação para a outra pessoa");
        }
        validarReferencia(dados);

        notificacao.setTitulo(dados.getTitulo());
        notificacao.setMensagem(dados.getMensagem());
        notificacao.setTipoEvento(dados.getTipoEvento());
        notificacao.setPrioridade(dados.getPrioridade());
        notificacao.setOrigem(dados.getOrigem());
        notificacao.setIdReferencia(dados.getIdReferencia());
        notificacao.setAgendadaPara(dados.getAgendadaPara());
        prepararEnvio(notificacao, dados);
        return notificacaoRepository.save(notificacao);
    }

    @Transactional
    public Notificacao alterarStatus(String id, StatusNotificacao novoStatus) {
        Notificacao notificacao = buscarPorId(id);
        StatusNotificacao atual = notificacao.getStatus();

        // Regra 7: transicoes permitidas por aqui: ENVIADA -> LIDA e PENDENTE -> CANCELADA.
        // ENVIADA e FALHA dependem do resultado dos envios (POST .../{id}/envios).
        if (novoStatus == StatusNotificacao.LIDA && atual == StatusNotificacao.ENVIADA) {
            notificacao.setLidaEm(LocalDateTime.now());
        } else if (novoStatus == StatusNotificacao.CANCELADA && atual == StatusNotificacao.PENDENTE) {
            notificacao.setAgendadaPara(null);
        } else if (novoStatus == StatusNotificacao.ENVIADA || novoStatus == StatusNotificacao.FALHA) {
            throw new NotificacaoException("O status " + novoStatus + " é definido pelo resultado dos envios. "
                    + "Registre o envio em POST /api/v1/notificacoes/notificacoes/" + id + "/envios");
        } else {
            throw new NotificacaoException("Não é possível mudar a notificação de " + atual + " para " + novoStatus
                    + ". Só é permitido ENVIADA -> LIDA e PENDENTE -> CANCELADA");
        }
        notificacao.setStatus(novoStatus);
        return notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void remover(String id) {
        Notificacao notificacao = buscarPorId(id);

        // Regra 8: notificacao que chegou na pessoa faz parte do historico
        if (notificacao.getStatus() == StatusNotificacao.ENVIADA || notificacao.getStatus() == StatusNotificacao.LIDA) {
            throw new NotificacaoException("A notificação já foi entregue (" + notificacao.getStatus()
                    + ") e faz parte do histórico da pessoa; ela não pode ser removida");
        }
        notificacaoRepository.delete(notificacao); // os envios saem junto (cascade)
    }

    // Confere canal, modelo, preferencia da pessoa e agendamento; usado no cadastro e na edicao
    private void prepararEnvio(Notificacao notificacao, Notificacao dados) {
        Canal canal = canalService.buscarPorId(dados.getCanal().getId()); // 404 se o canal nao existir
        Modelo modelo = dados.getModelo() != null
                ? modeloService.buscarPorId(dados.getModelo().getId()) // 404 se o modelo nao existir
                : null;

        // Regra 1: canal desativado nao recebe notificacoes
        if (!Boolean.TRUE.equals(canal.getAtivo())) {
            throw new NotificacaoException("O canal " + canal.getNome() + " está desativado e não pode receber notificações");
        }

        // Regra 2: respeitar a preferencia da pessoa (sem preferencia cadastrada, recebe)
        Optional<Preferencia> preferencia = preferenciaService.buscarPreferencia(
                notificacao.getIdPessoa(), canal.getId(), dados.getTipoEvento());
        if (preferencia.isPresent() && !Boolean.TRUE.equals(preferencia.get().getHabilitado())) {
            throw new NotificacaoException("A pessoa desabilitou as notificações de " + dados.getTipoEvento()
                    + " pelo canal " + canal.getNome());
        }

        // Regra 3: o modelo tem que estar ativo e ser do mesmo tipo de evento
        if (modelo != null) {
            if (!Boolean.TRUE.equals(modelo.getAtivo())) {
                throw new NotificacaoException("O modelo " + modelo.getCodigo() + " está desativado");
            }
            if (modelo.getTipoEvento() != dados.getTipoEvento()) {
                throw new NotificacaoException("O modelo " + modelo.getCodigo() + " é do evento " + modelo.getTipoEvento()
                        + " e não pode ser usado em uma notificação de " + dados.getTipoEvento());
            }
        }

        // Regra 4: nao agendar no passado
        if (dados.getAgendadaPara() != null && dados.getAgendadaPara().isBefore(LocalDateTime.now())) {
            throw new NotificacaoException("A data de agendamento (agendadaPara) não pode estar no passado");
        }

        notificacao.setCanal(canal);
        notificacao.setModelo(modelo);
    }

    // Integracao: o idReferencia tem que existir no modulo de origem (404 se nao existir).
    // Origens cujo modulo ainda nao tem Service na development nao sao conferidas.
    private void validarReferencia(Notificacao notificacao) {
        String idReferencia = notificacao.getIdReferencia();
        if (idReferencia == null || idReferencia.isBlank()) {
            return;
        }
        switch (notificacao.getOrigem()) {
            case PESSOAS -> usuarioService.buscarPorId(idReferencia);
            case CONTAS -> contaBancariaService.buscarPorId(idReferencia);
            case EDUCACAO -> conteudoEducacionalService.buscarPorId(idReferencia);
            case NOTIFICACOES -> buscarPorId(idReferencia);
            default -> {
                // TRANSACOES, INVESTIMENTOS, DIVIDAS e PLANEJAMENTOS: o id fica so registrado
            }
        }
    }
}
