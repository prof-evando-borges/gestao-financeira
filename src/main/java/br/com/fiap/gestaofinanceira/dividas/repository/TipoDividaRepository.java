package br.com.fiap.gestaofinanceira.dividas.repository;

import br.com.fiap.gestaofinanceira.dividas.entity.TipoDivida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoDividaRepository extends JpaRepository<TipoDivida, String> {
}
