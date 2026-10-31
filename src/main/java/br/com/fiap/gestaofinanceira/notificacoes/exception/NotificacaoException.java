package br.com.fiap.gestaofinanceira.notificacoes.exception;

/**
 * Regra de negócio do módulo de Controle de Notificações violada.
 * Tratada pelo {@link NotificacaoExceptionHandler}, que responde 422.
 */
public class NotificacaoException extends RuntimeException {

    public NotificacaoException(String mensagem) {
        super(mensagem);
    }

    public NotificacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
