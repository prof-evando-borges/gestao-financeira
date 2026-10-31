package br.com.fiap.gestaofinanceira.investimentos.entity;

/**
 * Perfil de investidor (suitability). Cada perfil aceita aplicacoes ate um risco maximo:
 * CONSERVADOR so risco baixo, MODERADO ate risco medio e ARROJADO qualquer risco.
 */
public enum PerfilInvestidor {
    CONSERVADOR(NivelRisco.BAIXO),
    MODERADO(NivelRisco.MEDIO),
    ARROJADO(NivelRisco.ALTO);

    private final NivelRisco riscoMaximo;

    PerfilInvestidor(NivelRisco riscoMaximo) {
        this.riscoMaximo = riscoMaximo;
    }

    public NivelRisco getRiscoMaximo() {
        return riscoMaximo;
    }

    public boolean permite(TipoInvestimento tipo) {
        return tipo.getRisco().compareTo(riscoMaximo) <= 0;
    }
}
