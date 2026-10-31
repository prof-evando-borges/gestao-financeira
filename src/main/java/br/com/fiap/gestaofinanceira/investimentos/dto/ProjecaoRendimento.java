package br.com.fiap.gestaofinanceira.investimentos.dto;

import java.math.BigDecimal;

/**
 * Resposta de GET /api/v1/investimentos/aplicacoes/{id}/projecao.
 */
public record ProjecaoRendimento(
        String idAplicacao,
        BigDecimal valorAplicado,
        BigDecimal taxaAnual,
        int meses,
        BigDecimal montanteProjetado,
        BigDecimal rendimentoProjetado
) {
}
