package br.com.fiap.gestaofinanceira.planejamentos.controller;

import br.com.fiap.gestaofinanceira.planejamentos.entity.MetaFinanceira;
import br.com.fiap.gestaofinanceira.planejamentos.service.MetaFinanceiraService;
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
@RequestMapping("/api/v1/planejamentos/metas")
public class MetaFinanceiraController {

    private final MetaFinanceiraService service;

    public MetaFinanceiraController(MetaFinanceiraService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetaFinanceira cadastrar(@Valid @RequestBody MetaFinanceira meta) {
        return service.cadastrar(meta);
    }

    @GetMapping
    public List<MetaFinanceira> listar(@RequestParam(required = false) String idUsuario) {
        if (idUsuario != null && !idUsuario.isBlank()) {
            return service.listarPorUsuario(idUsuario);
        }
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public MetaFinanceira buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/busca/atrasadas")
    public List<MetaFinanceira> listarAtrasadas() {
        return service.listarAtrasadas();
    }

    @PutMapping("/{id}")
    public MetaFinanceira atualizar(@PathVariable String id, @Valid @RequestBody MetaFinanceira meta) {
        return service.atualizar(id, meta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        service.remover(id);
    }
}