package br.com.fiap.gestaofinanceira.educacao.exception;

/**
 * Regra de negócio do módulo de Educação Financeira violada.
 * Tratada pelo {@link EducacaoExceptionHandler}, que responde 422.
 */
public class EducacaoException extends RuntimeException {

    public EducacaoException(String mensagem) {
        super(mensagem);
    }

    public EducacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
