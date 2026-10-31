package br.com.fiap.gestaofinanceira.transacoes.controller;

import br.com.fiap.gestaofinanceira.transacoes.entity.TipoTransacao;
import br.com.fiap.gestaofinanceira.transacoes.entity.Transacao;
import br.com.fiap.gestaofinanceira.transacoes.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacoes/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transacao cadastrar(@Valid @RequestBody Transacao transacao) {
        return transacaoService.cadastrar(transacao);
    }

    // GET /api/v1/transacoes/transacoes?tipo=DESPESA&idPessoa=... (os dois filtros sao opcionais)
    @GetMapping
    public List<Transacao> listar(@RequestParam(required = false) TipoTransacao tipo,
                                  @RequestParam(required = false) String idPessoa) {
        return transacaoService.listar(tipo, idPessoa);
    }

    @GetMapping("/{id}")
    public Transacao buscarPorId(@PathVariable String id) {
        return transacaoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Transacao atualizar(@PathVariable String id, @Valid @RequestBody Transacao transacao) {
        return transacaoService.atualizar(id, transacao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        transacaoService.remover(id);
    }
}
