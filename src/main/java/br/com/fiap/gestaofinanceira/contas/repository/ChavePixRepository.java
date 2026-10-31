package br.com.fiap.gestaofinanceira.contas.repository;

import br.com.fiap.gestaofinanceira.contas.entity.ChavePix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChavePixRepository extends JpaRepository<ChavePix, String> {

    List<ChavePix> findByContaId(String idConta);

    long countByContaId(String idConta);

    boolean existsByValor(String valor);

    boolean existsByValorAndIdNot(String valor, String id);
}
