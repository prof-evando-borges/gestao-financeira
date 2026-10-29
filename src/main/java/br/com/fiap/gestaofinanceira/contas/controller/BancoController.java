package br.com.fiap.gestaofinanceira.contas.controller;

import br.com.fiap.gestaofinanceira.contas.entity.Banco;
import br.com.fiap.gestaofinanceira.contas.service.BancoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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
@RequestMapping("/api/v1/contas/bancos")
public class BancoController {

    private final BancoService bancoService;

    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Banco cadastrar(@Valid @RequestBody Banco banco) {
        return bancoService.cadastrar(banco);
    }

    // GET /api/v1/contas/bancos?codigo=341 filtra pelo codigo COMPE
    @GetMapping
    public List<Banco> listar(
            @RequestParam(required = false)
            @Pattern(regexp = "\\d{3}", message = "O código do banco deve ter 3 dígitos (ex.: 001)") String codigo) {
        return bancoService.listar(codigo);
    }

    @GetMapping("/{id}")
    public Banco buscarPorId(@PathVariable String id) {
        return bancoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Banco atualizar(@PathVariable String id, @Valid @RequestBody Banco banco) {
        return bancoService.atualizar(id, banco);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        bancoService.remover(id);
    }
}
