package br.com.fiap.gestaofinanceira.investimentos.repository;

import br.com.fiap.gestaofinanceira.investimentos.entity.Aplicacao;
import br.com.fiap.gestaofinanceira.investimentos.entity.StatusAplicacao;
import br.com.fiap.gestaofinanceira.investimentos.entity.TipoInvestimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AplicacaoRepository extends JpaRepository<Aplicacao, String> {

    // GET /api/v1/investimentos/aplicacoes?idInvestidor=...
    List<Aplicacao> findByInvestidorId(String idInvestidor);

    // GET /api/v1/investimentos/aplicacoes?tipo=ACOES
    List<Aplicacao> findByTipo(TipoInvestimento tipo);

    // Carteira do investidor e regra de troca de perfil (so olham as aplicacoes ativas)
    List<Aplicacao> findByInvestidorIdAndStatus(String idInvestidor, StatusAplicacao status);

    // Regra: investidor com aplicacao ativa nao pode ser removido
    boolean existsByInvestidorIdAndStatus(String idInvestidor, StatusAplicacao status);
}
