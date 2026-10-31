package br.com.fiap.gestaofinanceira.dividas.repository;

import br.com.fiap.gestaofinanceira.dividas.entity.Divida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DividaRepository extends JpaRepository<Divida, String> {
}
