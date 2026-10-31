package br.com.fiap.gestaofinanceira.contas.controller;

import br.com.fiap.gestaofinanceira.contas.entity.ContaBancaria;
import br.com.fiap.gestaofinanceira.contas.entity.StatusConta;
import br.com.fiap.gestaofinanceira.contas.service.ContaBancariaService;
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
@RequestMapping("/api/v1/contas/contas-bancarias")
public class ContaBancariaController {

    private final ContaBancariaService contaBancariaService;

    public ContaBancariaController(ContaBancariaService contaBancariaService) {
        this.contaBancariaService = contaBancariaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaBancaria cadastrar(@Valid @RequestBody ContaBancaria conta) {
        return contaBancariaService.cadastrar(conta);
    }

    // GET /api/v1/contas/contas-bancarias?idUsuario=...&status=ATIVA (os dois filtros sao opcionais)
    @GetMapping
    public List<ContaBancaria> listar(@RequestParam(required = false) String idUsuario,
                                      @RequestParam(required = false) StatusConta status) {
        return contaBancariaService.listar(idUsuario, status);
    }

    @GetMapping("/{id}")
    public ContaBancaria buscarPorId(@PathVariable String id) {
        return contaBancariaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ContaBancaria atualizar(@PathVariable String id, @Valid @RequestBody ContaBancaria conta) {
        return contaBancariaService.atualizar(id, conta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        contaBancariaService.remover(id);
    }
}
