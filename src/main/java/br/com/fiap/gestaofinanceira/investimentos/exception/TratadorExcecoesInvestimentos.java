package br.com.fiap.gestaofinanceira.investimentos.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tratador de excecoes do modulo, limitado aos controllers do pacote de Investimentos.
 * 400 (Bean Validation) e 404 (RecursoNaoEncontradoException) continuam com o
 * TratadorGlobalDeExcecoes do pacote compartilhado.
 */
@RestControllerAdvice(basePackages = "br.com.fiap.gestaofinanceira.investimentos")
public class TratadorExcecoesInvestimentos {

    @ExceptionHandler(InvestimentoException.class)
    public ProblemDetail tratarRegraDeNegocio(InvestimentoException ex) {
        // 422 Unprocessable Content
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
        problema.setTitle("Regra de negócio violada");
        problema.setProperty("modulo", "investimentos");
        return problema;
    }
}
