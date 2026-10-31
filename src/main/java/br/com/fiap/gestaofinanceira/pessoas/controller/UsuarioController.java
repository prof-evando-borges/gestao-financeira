package br.com.fiap.gestaofinanceira.pessoas.controller;

import br.com.fiap.gestaofinanceira.pessoas.entities.Usuario;
import br.com.fiap.gestaofinanceira.pessoas.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// As anotacoes nos parametros (@Pattern, @Size) sao validadas pelo Spring MVC -> 400
@RestController
@RequestMapping("/api/v1/pessoas/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario cadastrar(@Valid @RequestBody Usuario usuario) {
        return usuarioService.cadastrar(usuario);
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable String id) {
        return usuarioService.buscarPorId(id);
    }

    // GET /api/v1/pessoas/usuarios/busca/cpf/12345678901
    @GetMapping("/busca/cpf/{cpf}")
    public Usuario buscarPorCpf(
            @PathVariable
            @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos, somente números") String cpf) {
        return usuarioService.buscarPorCpf(cpf);
    }

    // GET /api/v1/pessoas/usuarios/busca?nome=ana
    @GetMapping("/busca")
    public List<Usuario> buscarPorNome(
            @RequestParam
            @Size(min = 2, message = "Informe pelo menos 2 letras do nome") String nome) {
        return usuarioService.buscarPorNome(nome);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable String id, @Valid @RequestBody Usuario usuario) {
        return usuarioService.atualizar(id, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        usuarioService.remover(id);
    }
}
