package br.com.fiap.gestaofinanceira.contas.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Trata as excecoes do modulo de Contas. Vale so para os controllers deste pacote;
 * 400 e 404 continuam com o TratadorGlobalDeExcecoes do pacote compartilhado.
 */
@RestControllerAdvice(basePackages = "br.com.fiap.gestaofinanceira.contas")
public class ContaExceptionHandler {

    @ExceptionHandler(ContaException.class)
    public ProblemDetail tratarContaException(ContaException ex) {
        // 422 Unprocessable Content: os dados sao validos, mas violam uma regra de negocio
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
    }
}
