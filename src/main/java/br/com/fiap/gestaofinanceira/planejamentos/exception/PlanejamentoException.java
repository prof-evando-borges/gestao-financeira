package br.com.fiap.gestaofinanceira.planejamentos.exception;

public class PlanejamentoException extends RuntimeException {

    public PlanejamentoException(String mensagem) {
        super(mensagem);
    }

    public PlanejamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}