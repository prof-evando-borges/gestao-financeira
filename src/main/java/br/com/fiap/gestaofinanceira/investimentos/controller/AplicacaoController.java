package br.com.fiap.gestaofinanceira.investimentos.controller;

import br.com.fiap.gestaofinanceira.investimentos.dto.ProjecaoRendimento;
import br.com.fiap.gestaofinanceira.investimentos.entity.Aplicacao;
import br.com.fiap.gestaofinanceira.investimentos.entity.TipoInvestimento;
import br.com.fiap.gestaofinanceira.investimentos.service.AplicacaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/api/v1/investimentos/aplicacoes")
public class AplicacaoController {

    private final AplicacaoService aplicacaoService;

    public AplicacaoController(AplicacaoService aplicacaoService) {
        this.aplicacaoService = aplicacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aplicacao cadastrar(@Valid @RequestBody Aplicacao aplicacao) {
        return aplicacaoService.cadastrar(aplicacao);
    }

    // GET /aplicacoes, /aplicacoes?idInvestidor=..., /aplicacoes?tipo=ACOES ou os dois filtros juntos
    @GetMapping
    public List<Aplicacao> listar(@RequestParam(name = "idInvestidor", required = false) String idInvestidor,
                                  @RequestParam(name = "tipo", required = false) TipoInvestimento tipo) {
        return aplicacaoService.listar(idInvestidor, tipo);
    }

    @GetMapping("/{id}")
    public Aplicacao buscarPorId(@PathVariable("id") String id) {
        return aplicacaoService.buscarPorId(id);
    }

    // GET /aplicacoes/{id}/projecao?meses=24 (juros compostos); meses fora de 1..600 -> 400
    @GetMapping("/{id}/projecao")
    public ProjecaoRendimento projetarRendimento(
            @PathVariable("id") String id,
            @RequestParam(name = "meses", defaultValue = "12")
            @Min(value = 1, message = "O prazo da projeção deve ser de pelo menos 1 mês.")
            @Max(value = 600, message = "O prazo da projeção pode ser de no máximo 600 meses.") int meses) {
        return aplicacaoService.projetarRendimento(id, meses);
    }

    @PutMapping("/{id}")
    public Aplicacao atualizar(@PathVariable("id") String id, @Valid @RequestBody Aplicacao aplicacao) {
        return aplicacaoService.atualizar(id, aplicacao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable("id") String id) {
        aplicacaoService.remover(id);
    }
}
