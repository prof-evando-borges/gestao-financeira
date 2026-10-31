package br.com.fiap.gestaofinanceira.investimentos.entity;

public enum TipoInvestimento {
    TESOURO_DIRETO(NivelRisco.BAIXO),
    RENDA_FIXA(NivelRisco.BAIXO),
    FUNDOS(NivelRisco.MEDIO),
    ACOES(NivelRisco.ALTO),
    CRIPTOMOEDAS(NivelRisco.ALTO);

    private final NivelRisco risco;

    TipoInvestimento(NivelRisco risco) {
        this.risco = risco;
    }

    public NivelRisco getRisco() {
        return risco;
    }
}
