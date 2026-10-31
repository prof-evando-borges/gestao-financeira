package br.com.fiap.gestaofinanceira.notificacoes.service;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Envio;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Notificacao;
import br.com.fiap.gestaofinanceira.notificacoes.entity.StatusEnvio;
import br.com.fiap.gestaofinanceira.notificacoes.entity.StatusNotificacao;
import br.com.fiap.gestaofinanceira.notificacoes.exception.NotificacaoException;
import br.com.fiap.gestaofinanceira.notificacoes.repository.EnvioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Histórico de tentativas de envio de cada notificação.
 * Um envio com SUCESSO marca a notificação como ENVIADA; na terceira FALHA ela fica como FALHA.
 */
@Service
public class EnvioService {

    public static final int MAXIMO_TENTATIVAS = 3;

    private final EnvioRepository envioRepository;
    private final NotificacaoService notificacaoService;

    public EnvioService(EnvioRepository envioRepository, NotificacaoService notificacaoService) {
        this.envioRepository = envioRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public Envio registrar(String idNotificacao, StatusEnvio status, String mensagemErro) {
        Notificacao notificacao = notificacaoService.buscarPorId(idNotificacao); // 404 se nao existir

        // Regra 1: so notificacao PENDENTE pode ser enviada
        if (notificacao.getStatus() != StatusNotificacao.PENDENTE) {
            throw new NotificacaoException("A notificação está " + notificacao.getStatus()
                    + " e não aceita novos envios; só notificações PENDENTES podem ser enviadas");
        }
        // Regra 2: notificacao agendada so sai a partir do horario agendado
        if (notificacao.getAgendadaPara() != null && notificacao.getAgendadaPara().isAfter(LocalDateTime.now())) {
            throw new NotificacaoException("A notificação está agendada para " + notificacao.getAgendadaPara()
                    + " e ainda não pode ser enviada");
        }
        // Regra 3: falha precisa do motivo, sucesso nao tem erro
        if (status == StatusEnvio.FALHA && (mensagemErro == null || mensagemErro.isBlank())) {
            throw new NotificacaoException("Informe o motivo da falha em mensagemErro");
        }
        if (status == StatusEnvio.SUCESSO && mensagemErro != null && !mensagemErro.isBlank()) {
            throw new NotificacaoException("Um envio com SUCESSO não pode ter mensagemErro");
        }

        int tentativa = (int) envioRepository.countByNotificacaoId(idNotificacao) + 1;

        Envio envio = new Envio();
        envio.setNotificacao(notificacao);
        envio.setNumeroTentativa(tentativa);
        envio.setStatus(status);
        envio.setMensagemErro(status == StatusEnvio.FALHA ? mensagemErro : null);
        Envio salvo = envioRepository.save(envio);

        if (status == StatusEnvio.SUCESSO) {
            notificacao.setStatus(StatusNotificacao.ENVIADA);
            notificacao.setEnviadaEm(salvo.getDataEnvio());
        } else if (tentativa >= MAXIMO_TENTATIVAS) {
            // Regra 4: depois de 3 falhas a notificacao desiste de ser enviada
            notificacao.setStatus(StatusNotificacao.FALHA);
        }
        return salvo;
    }

    public List<Envio> listarPorNotificacao(String idNotificacao) {
        notificacaoService.buscarPorId(idNotificacao); // 404 se a notificacao nao existir
        return envioRepository.findByNotificacaoIdOrderByNumeroTentativaAsc(idNotificacao);
    }
}
