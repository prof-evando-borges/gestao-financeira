package br.com.fiap.gestaofinanceira.planejamentos.controller;

import br.com.fiap.gestaofinanceira.planejamentos.entity.Aporte;
import br.com.fiap.gestaofinanceira.planejamentos.service.AporteService;
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
@RequestMapping("/api/v1/planejamentos/aportes")
public class AporteController {

    private final AporteService service;

    public AporteController(AporteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aporte cadastrar(@Valid @RequestBody Aporte aporte) {
        return service.cadastrar(aporte);
    }

    @GetMapping
    public List<Aporte> listar(@RequestParam(required = false) String idMeta) {
        if (idMeta != null && !idMeta.isBlank()) {
            return service.listarPorMeta(idMeta);
        }
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Aporte buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Aporte atualizar(@PathVariable String id, @Valid @RequestBody Aporte aporte) {
        return service.atualizar(id, aporte);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        service.remover(id);
    }
}