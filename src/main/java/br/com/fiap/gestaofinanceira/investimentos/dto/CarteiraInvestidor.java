package br.com.fiap.gestaofinanceira.investimentos.dto;

import br.com.fiap.gestaofinanceira.investimentos.entity.PerfilInvestidor;
import br.com.fiap.gestaofinanceira.investimentos.entity.TipoInvestimento;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Resposta de GET /api/v1/investimentos/investidores/{id}/carteira (considera so as aplicacoes ativas).
 */
public record CarteiraInvestidor(
        String idInvestidor,
        PerfilInvestidor perfil,
        int quantidadeAplicacoesAtivas,
        BigDecimal totalAplicado,
        Map<TipoInvestimento, BigDecimal> totalPorTipo,
        BigDecimal montanteProjetado12Meses
) {
}
