package br.com.fiap.gestaofinanceira.dividas.repository;

import br.com.fiap.gestaofinanceira.dividas.entity.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
}
