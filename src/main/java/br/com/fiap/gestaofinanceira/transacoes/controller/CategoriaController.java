package br.com.fiap.gestaofinanceira.transacoes.controller;

import br.com.fiap.gestaofinanceira.transacoes.entity.Categoria;
import br.com.fiap.gestaofinanceira.transacoes.service.CategoriaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/v1/transacoes/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria cadastrar(@Valid @RequestBody Categoria categoria) {
        return categoriaService.cadastrar(categoria);
    }

    @GetMapping
    public List<Categoria> listarTodos() {
        return categoriaService.listarTodos();
    }

    @GetMapping("/{id}")
    public Categoria buscarPorId(@PathVariable String id) {
        return categoriaService.buscarPorId(id);
    }

    // GET /api/v1/transacoes/categorias/busca?nome=Salario
    @GetMapping("/busca")
    public Categoria buscarPorNome(
            @RequestParam @NotBlank(message = "Informe o nome da categoria") String nome) {
        return categoriaService.buscarPorNome(nome);
    }

    @PutMapping("/{id}")
    public Categoria atualizar(@PathVariable String id, @Valid @RequestBody Categoria categoria) {
        return categoriaService.atualizar(id, categoria);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        categoriaService.remover(id);
    }
}
