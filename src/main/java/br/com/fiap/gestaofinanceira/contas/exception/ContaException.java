package br.com.fiap.gestaofinanceira.contas.exception;

/**
 * Regra de negocio do modulo de Contas violada.
 * O {@link ContaExceptionHandler} converte em HTTP 422.
 */
public class ContaException extends RuntimeException {

    public ContaException(String mensagem) {
        super(mensagem);
    }

    public ContaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
