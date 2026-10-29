package br.com.fiap.gestaofinanceira.notificacoes.controller;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Modelo;
import br.com.fiap.gestaofinanceira.notificacoes.entity.TipoEvento;
import br.com.fiap.gestaofinanceira.notificacoes.service.ModeloService;
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

// Corpo: { "codigo": "PARCELA_VENCENDO", "titulo": "Parcela vencendo", "conteudo": "Olá {nome}, ...", "tipoEvento": "PARCELA_DIVIDA", "ativo": true }
@RestController
@RequestMapping("/api/v1/notificacoes/modelos")
public class ModeloController {

    private final ModeloService modeloService;

    public ModeloController(ModeloService modeloService) {
        this.modeloService = modeloService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Modelo cadastrar(@Valid @RequestBody Modelo modelo) {
        return modeloService.cadastrar(modelo);
    }

    // GET /api/v1/notificacoes/modelos                           -> todos
    // GET /api/v1/notificacoes/modelos?tipoEvento=PARCELA_DIVIDA -> so os do evento (400 se o evento nao existir)
    @GetMapping
    public List<Modelo> listar(@RequestParam(required = false) TipoEvento tipoEvento) {
        if (tipoEvento != null) {
            return modeloService.listarPorTipoEvento(tipoEvento);
        }
        return modeloService.listarTodos();
    }

    @GetMapping("/{id}")
    public Modelo buscarPorId(@PathVariable String id) {
        return modeloService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Modelo atualizar(@PathVariable String id, @Valid @RequestBody Modelo modelo) {
        return modeloService.atualizar(id, modelo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        modeloService.remover(id);
    }
}
