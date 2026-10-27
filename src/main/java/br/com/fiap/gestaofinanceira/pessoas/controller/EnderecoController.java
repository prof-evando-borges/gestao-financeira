package br.com.fiap.gestaofinanceira.pessoas.controller;

import br.com.fiap.gestaofinanceira.pessoas.entities.Endereco;
import br.com.fiap.gestaofinanceira.pessoas.service.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pessoas/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endereco cadastrar(@Valid @RequestBody Endereco endereco) {
        return enderecoService.cadastrar(endereco);
    }

    // GET /api/v1/pessoas/enderecos               -> todos
    // GET /api/v1/pessoas/enderecos?idUsuario=... -> so os do usuario
    @GetMapping
    public List<Endereco> listar(@RequestParam(required = false) String idUsuario) {
        if (idUsuario != null) {
            return enderecoService.listarPorUsuario(idUsuario);
        }
        return enderecoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Endereco buscarPorId(@PathVariable String id) {
        return enderecoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Endereco atualizar(@PathVariable String id, @Valid @RequestBody Endereco endereco) {
        return enderecoService.atualizar(id, endereco);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        enderecoService.remover(id);
    }
}
