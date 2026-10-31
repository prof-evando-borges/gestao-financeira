package br.com.fiap.gestaofinanceira.notificacoes.controller;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Canal;
import br.com.fiap.gestaofinanceira.notificacoes.service.CanalService;
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

// Corpo: { "nome": "EMAIL", "descricao": "Envio por e-mail", "ativo": true }
@RestController
@RequestMapping("/api/v1/notificacoes/canais")
public class CanalController {

    private final CanalService canalService;

    public CanalController(CanalService canalService) {
        this.canalService = canalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Canal cadastrar(@Valid @RequestBody Canal canal) {
        return canalService.cadastrar(canal);
    }

    // GET /api/v1/notificacoes/canais             -> todos
    // GET /api/v1/notificacoes/canais?ativo=true  -> so os ativos (ou desativados com false)
    @GetMapping
    public List<Canal> listar(@RequestParam(required = false) Boolean ativo) {
        if (ativo != null) {
            return canalService.listarPorAtivo(ativo);
        }
        return canalService.listarTodos();
    }

    @GetMapping("/{id}")
    public Canal buscarPorId(@PathVariable String id) {
        return canalService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Canal atualizar(@PathVariable String id, @Valid @RequestBody Canal canal) {
        return canalService.atualizar(id, canal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        canalService.remover(id);
    }
}
