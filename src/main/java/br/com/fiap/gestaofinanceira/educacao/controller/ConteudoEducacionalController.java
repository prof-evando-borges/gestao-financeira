package br.com.fiap.gestaofinanceira.educacao.controller;

import br.com.fiap.gestaofinanceira.educacao.entity.ConteudoEducacional;
import br.com.fiap.gestaofinanceira.educacao.entity.NivelConhecimento;
import br.com.fiap.gestaofinanceira.educacao.service.ConteudoEducacionalService;
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

// O corpo precisa do campo "tipo": ARTIGO (com "autor"), VIDEO (com "urlVideo") ou CURSO (com "numeroModulos")
@RestController
@RequestMapping("/api/v1/educacao/conteudos")
public class ConteudoEducacionalController {

    private final ConteudoEducacionalService conteudoService;

    public ConteudoEducacionalController(ConteudoEducacionalService conteudoService) {
        this.conteudoService = conteudoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConteudoEducacional cadastrar(@Valid @RequestBody ConteudoEducacional conteudo) {
        return conteudoService.cadastrar(conteudo);
    }

    // GET /api/v1/educacao/conteudos                 -> todos
    // GET /api/v1/educacao/conteudos?nivel=INICIANTE -> so os do nivel (400 se o nivel nao existir)
    @GetMapping
    public List<ConteudoEducacional> listar(@RequestParam(required = false) NivelConhecimento nivel) {
        if (nivel != null) {
            return conteudoService.listarPorNivel(nivel);
        }
        return conteudoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ConteudoEducacional buscarPorId(@PathVariable String id) {
        return conteudoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ConteudoEducacional atualizar(@PathVariable String id,
                                         @Valid @RequestBody ConteudoEducacional conteudo) {
        return conteudoService.atualizar(id, conteudo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        conteudoService.remover(id);
    }
}
