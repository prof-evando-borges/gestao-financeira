package br.com.fiap.gestaofinanceira.investimentos.repository;

import br.com.fiap.gestaofinanceira.investimentos.entity.Investidor;
import br.com.fiap.gestaofinanceira.investimentos.entity.PerfilInvestidor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestidorRepository extends JpaRepository<Investidor, String> {

    // Regra: uma pessoa so pode ter um perfil de investidor
    boolean existsByIdPessoa(String idPessoa);

    // GET /api/v1/investimentos/investidores?perfil=MODERADO
    List<Investidor> findByPerfil(PerfilInvestidor perfil);
}
