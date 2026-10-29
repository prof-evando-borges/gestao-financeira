package br.com.fiap.gestaofinanceira.investimentos.exception;

/**
 * Regra de negocio do modulo de Investimentos violada.
 * O {@link TratadorExcecoesInvestimentos} converte em HTTP 422.
 */
public class InvestimentoException extends RuntimeException {

    public InvestimentoException(String mensagem) {
        super(mensagem);
    }

    public InvestimentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
