package br.com.fiap.gestaofinanceira.notificacoes.controller;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Preferencia;
import br.com.fiap.gestaofinanceira.notificacoes.service.PreferenciaService;
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

// Corpo: { "idPessoa": "...", "canal": { "id": "..." }, "tipoEvento": "PARCELA_DIVIDA", "habilitado": true, "antecedenciaDias": 3 }
@RestController
@RequestMapping("/api/v1/notificacoes/preferencias")
public class PreferenciaController {

    private final PreferenciaService preferenciaService;

    public PreferenciaController(PreferenciaService preferenciaService) {
        this.preferenciaService = preferenciaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Preferencia cadastrar(@Valid @RequestBody Preferencia preferencia) {
        return preferenciaService.cadastrar(preferencia);
    }

    // GET /api/v1/notificacoes/preferencias               -> todas
    // GET /api/v1/notificacoes/preferencias?idPessoa=...  -> so as da pessoa (404 se a pessoa nao existir)
    @GetMapping
    public List<Preferencia> listar(@RequestParam(required = false) String idPessoa) {
        if (idPessoa != null) {
            return preferenciaService.listarPorPessoa(idPessoa);
        }
        return preferenciaService.listarTodos();
    }

    @GetMapping("/{id}")
    public Preferencia buscarPorId(@PathVariable String id) {
        return preferenciaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Preferencia atualizar(@PathVariable String id, @Valid @RequestBody Preferencia preferencia) {
        return preferenciaService.atualizar(id, preferencia);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        preferenciaService.remover(id);
    }
}
