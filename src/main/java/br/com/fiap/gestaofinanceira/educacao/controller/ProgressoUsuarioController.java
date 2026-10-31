package br.com.fiap.gestaofinanceira.educacao.controller;

import br.com.fiap.gestaofinanceira.educacao.entity.ProgressoUsuario;
import br.com.fiap.gestaofinanceira.educacao.entity.StatusProgresso;
import br.com.fiap.gestaofinanceira.educacao.service.ProgressoUsuarioService;
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

// Corpo: { "idUsuario": "...", "idConteudo": "...", "status": "EM_ANDAMENTO", "percentual": 40, "dataInicio": "2026-10-01" }
@RestController
@RequestMapping("/api/v1/educacao/progressos")
public class ProgressoUsuarioController {

    private final ProgressoUsuarioService progressoService;

    public ProgressoUsuarioController(ProgressoUsuarioService progressoService) {
        this.progressoService = progressoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProgressoUsuario cadastrar(@Valid @RequestBody ProgressoUsuario progresso) {
        return progressoService.cadastrar(progresso);
    }

    // GET /api/v1/educacao/progressos                    -> todos
    // GET /api/v1/educacao/progressos?idUsuario=...      -> so os do usuario (404 se o usuario nao existir)
    // GET /api/v1/educacao/progressos?status=CONCLUIDO   -> so os do status (400 se o status nao existir)
    @GetMapping
    public List<ProgressoUsuario> listar(@RequestParam(required = false) String idUsuario,
                                         @RequestParam(required = false) StatusProgresso status) {
        if (idUsuario != null) {
            return progressoService.listarPorUsuario(idUsuario);
        }
        if (status != null) {
            return progressoService.listarPorStatus(status);
        }
        return progressoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ProgressoUsuario buscarPorId(@PathVariable String id) {
        return progressoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ProgressoUsuario atualizar(@PathVariable String id, @Valid @RequestBody ProgressoUsuario progresso) {
        return progressoService.atualizar(id, progresso);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        progressoService.remover(id);
    }
}
