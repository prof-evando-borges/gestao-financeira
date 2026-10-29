package br.com.fiap.gestaofinanceira.notificacoes.dto;

import br.com.fiap.gestaofinanceira.notificacoes.entity.StatusNotificacao;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo do PATCH /api/v1/notificacoes/notificacoes/{id}/status.
 * Ex.: { "status": "LIDA" } ou { "status": "CANCELADA" }
 */
public record AlteracaoStatusRequest(
        @NotNull(message = "Informe o novo status: LIDA ou CANCELADA")
        StatusNotificacao status
) {
}
