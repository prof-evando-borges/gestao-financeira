package br.com.fiap.gestaofinanceira.notificacoes.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Trata os erros de negócio do módulo de Controle de Notificações.
 * Vale apenas para os controllers do pacote notificacoes (basePackages).
 * 400 (Bean Validation) e 404 (RecursoNaoEncontradoException) continuam com o
 * TratadorGlobalDeExcecoes do pacote compartilhado.
 */
@RestControllerAdvice(basePackages = "br.com.fiap.gestaofinanceira.notificacoes")
public class NotificacaoExceptionHandler {

    @ExceptionHandler(NotificacaoException.class)
    public ProblemDetail tratarNotificacaoException(NotificacaoException ex) {
        // 422 Unprocessable Content
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
        problema.setTitle("Regra de negócio violada");
        problema.setProperty("modulo", "notificacoes");
        return problema;
    }
}
