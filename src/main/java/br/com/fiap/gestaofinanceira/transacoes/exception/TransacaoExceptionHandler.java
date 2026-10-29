package br.com.fiap.gestaofinanceira.transacoes.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Trata as excecoes do modulo de Transacoes. Vale so para os controllers deste pacote;
 * 400 e 404 continuam com o TratadorGlobalDeExcecoes do pacote compartilhado.
 */
@RestControllerAdvice(basePackages = "br.com.fiap.gestaofinanceira.transacoes")
public class TransacaoExceptionHandler {

    @ExceptionHandler(TransacaoException.class)
    public ProblemDetail tratarTransacaoException(TransacaoException ex) {
        // 422 Unprocessable Content: os dados sao validos, mas violam uma regra de negocio
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
    }
}
