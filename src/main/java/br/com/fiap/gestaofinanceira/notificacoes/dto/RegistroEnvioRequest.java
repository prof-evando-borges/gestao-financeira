package br.com.fiap.gestaofinanceira.notificacoes.dto;

import br.com.fiap.gestaofinanceira.notificacoes.entity.StatusEnvio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corpo do POST /api/v1/notificacoes/notificacoes/{id}/envios.
 * Ex.: { "status": "SUCESSO" } ou { "status": "FALHA", "mensagemErro": "Caixa de e-mail cheia" }
 */
public record RegistroEnvioRequest(
        @NotNull(message = "Informe o resultado do envio: SUCESSO ou FALHA")
        StatusEnvio status,

        @Size(max = 500, message = "A mensagem de erro deve ter no máximo 500 caracteres")
        String mensagemErro
) {
}
