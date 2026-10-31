package br.com.fiap.gestaofinanceira.contas.controller;

import br.com.fiap.gestaofinanceira.contas.entity.ChavePix;
import br.com.fiap.gestaofinanceira.contas.service.ChavePixService;
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
@RequestMapping("/api/v1/contas/chaves-pix")
public class ChavePixController {

    private final ChavePixService chavePixService;

    public ChavePixController(ChavePixService chavePixService) {
        this.chavePixService = chavePixService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChavePix cadastrar(@Valid @RequestBody ChavePix chave) {
        return chavePixService.cadastrar(chave);
    }

    // GET /api/v1/contas/chaves-pix?idConta=... lista as chaves de uma conta
    @GetMapping
    public List<ChavePix> listar(@RequestParam(required = false) String idConta) {
        return chavePixService.listar(idConta);
    }

    @GetMapping("/{id}")
    public ChavePix buscarPorId(@PathVariable String id) {
        return chavePixService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ChavePix atualizar(@PathVariable String id, @Valid @RequestBody ChavePix chave) {
        return chavePixService.atualizar(id, chave);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        chavePixService.remover(id);
    }
}
