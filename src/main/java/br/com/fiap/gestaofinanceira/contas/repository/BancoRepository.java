package br.com.fiap.gestaofinanceira.contas.repository;

import br.com.fiap.gestaofinanceira.contas.entity.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BancoRepository extends JpaRepository<Banco, String> {

    List<Banco> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, String id);
}
