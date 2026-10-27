package br.com.fiap.gestaofinanceira.pessoas.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Trata os erros de negocio do modulo de Pessoas.
 * Vale apenas para os controllers do pacote pessoas (basePackages).
 */
@RestControllerAdvice(basePackages = "br.com.fiap.gestaofinanceira.pessoas")
public class PessoaExceptionHandler {

    @ExceptionHandler(PessoaException.class)
    public ProblemDetail tratarPessoaException(PessoaException ex) {
        // 422 Unprocessable Content
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
        problema.setTitle("Regra de negócio violada");
        return problema;
    }
}
