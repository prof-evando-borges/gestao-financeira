package br.com.fiap.gestaofinanceira.pessoas.exception;

/**
 * Excecao das regras de negocio do modulo de Pessoas.
 * Tratada pelo {@link PessoaExceptionHandler}, que responde 422.
 */
public class PessoaException extends RuntimeException {

    public PessoaException(String mensagem) {
        super(mensagem);
    }

    public PessoaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
