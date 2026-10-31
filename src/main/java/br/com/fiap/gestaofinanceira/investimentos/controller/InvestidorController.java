package br.com.fiap.gestaofinanceira.investimentos.controller;

import br.com.fiap.gestaofinanceira.investimentos.dto.CarteiraInvestidor;
import br.com.fiap.gestaofinanceira.investimentos.entity.Investidor;
import br.com.fiap.gestaofinanceira.investimentos.entity.PerfilInvestidor;
import br.com.fiap.gestaofinanceira.investimentos.service.InvestidorService;
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
@RequestMapping("/api/v1/investimentos/investidores")
public class InvestidorController {

    private final InvestidorService investidorService;

    public InvestidorController(InvestidorService investidorService) {
        this.investidorService = investidorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Investidor cadastrar(@Valid @RequestBody Investidor investidor) {
        return investidorService.cadastrar(investidor);
    }

    // GET /investidores ou GET /investidores?perfil=MODERADO
    @GetMapping
    public List<Investidor> listar(@RequestParam(name = "perfil", required = false) PerfilInvestidor perfil) {
        return perfil == null ? investidorService.listarTodos() : investidorService.listarPorPerfil(perfil);
    }

    @GetMapping("/{id}")
    public Investidor buscarPorId(@PathVariable("id") String id) {
        return investidorService.buscarPorId(id);
    }

    @GetMapping("/{id}/carteira")
    public CarteiraInvestidor consultarCarteira(@PathVariable("id") String id) {
        return investidorService.consultarCarteira(id);
    }

    @PutMapping("/{id}")
    public Investidor atualizar(@PathVariable("id") String id, @Valid @RequestBody Investidor investidor) {
        return investidorService.atualizar(id, investidor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable("id") String id) {
        investidorService.remover(id);
    }
}
