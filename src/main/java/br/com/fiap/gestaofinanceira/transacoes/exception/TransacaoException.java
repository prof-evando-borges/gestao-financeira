package br.com.fiap.gestaofinanceira.transacoes.exception;

/**
 * Regra de negocio do modulo de Transacoes violada.
 * O {@link TransacaoExceptionHandler} converte em HTTP 422.
 */
public class TransacaoException extends RuntimeException {

    public TransacaoException(String mensagem) {
        super(mensagem);
    }

    public TransacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
