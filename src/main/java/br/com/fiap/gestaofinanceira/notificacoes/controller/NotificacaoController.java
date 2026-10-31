package br.com.fiap.gestaofinanceira.notificacoes.controller;

import br.com.fiap.gestaofinanceira.notificacoes.dto.AlteracaoStatusRequest;
import br.com.fiap.gestaofinanceira.notificacoes.dto.RegistroEnvioRequest;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Envio;
import br.com.fiap.gestaofinanceira.notificacoes.entity.Notificacao;
import br.com.fiap.gestaofinanceira.notificacoes.entity.StatusNotificacao;
import br.com.fiap.gestaofinanceira.notificacoes.service.EnvioService;
import br.com.fiap.gestaofinanceira.notificacoes.service.NotificacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Corpo: { "idPessoa": "...", "canal": { "id": "..." }, "modelo": { "id": "..." }, "titulo": "...", "mensagem": "...",
//          "tipoEvento": "SALDO_BAIXO", "prioridade": "ALTA", "origem": "CONTAS", "idReferencia": "...", "agendadaPara": null }
@RestController
@RequestMapping("/api/v1/notificacoes/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final EnvioService envioService;

    public NotificacaoController(NotificacaoService notificacaoService, EnvioService envioService) {
        this.notificacaoService = notificacaoService;
        this.envioService = envioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notificacao cadastrar(@Valid @RequestBody Notificacao notificacao) {
        return notificacaoService.cadastrar(notificacao);
    }

    // GET /api/v1/notificacoes/notificacoes                          -> todas
    // GET /api/v1/notificacoes/notificacoes?idPessoa=...             -> caixa de entrada da pessoa (404 se nao existir)
    // GET /api/v1/notificacoes/notificacoes?status=PENDENTE          -> so as do status (400 se o status nao existir)
    // GET /api/v1/notificacoes/notificacoes?idPessoa=...&status=LIDA -> os dois filtros juntos
    @GetMapping
    public List<Notificacao> listar(@RequestParam(required = false) String idPessoa,
                                    @RequestParam(required = false) StatusNotificacao status) {
        return notificacaoService.listar(idPessoa, status);
    }

    @GetMapping("/{id}")
    public Notificacao buscarPorId(@PathVariable String id) {
        return notificacaoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Notificacao atualizar(@PathVariable String id, @Valid @RequestBody Notificacao notificacao) {
        return notificacaoService.atualizar(id, notificacao);
    }

    // Corpo: { "status": "LIDA" } ou { "status": "CANCELADA" }
    @PatchMapping("/{id}/status")
    public Notificacao alterarStatus(@PathVariable String id, @Valid @RequestBody AlteracaoStatusRequest corpo) {
        return notificacaoService.alterarStatus(id, corpo.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        notificacaoService.remover(id);
    }

    // Historico de envios da notificacao (sub-recurso)
    // Corpo: { "status": "SUCESSO" } ou { "status": "FALHA", "mensagemErro": "..." }
    @PostMapping("/{id}/envios")
    @ResponseStatus(HttpStatus.CREATED)
    public Envio registrarEnvio(@PathVariable String id, @Valid @RequestBody RegistroEnvioRequest corpo) {
        return envioService.registrar(id, corpo.status(), corpo.mensagemErro());
    }

    @GetMapping("/{id}/envios")
    public List<Envio> listarEnvios(@PathVariable String id) {
        return envioService.listarPorNotificacao(id);
    }
}
