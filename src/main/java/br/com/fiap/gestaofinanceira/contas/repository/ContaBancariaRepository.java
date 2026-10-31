package br.com.fiap.gestaofinanceira.contas.repository;

import br.com.fiap.gestaofinanceira.contas.entity.ContaBancaria;
import br.com.fiap.gestaofinanceira.contas.entity.StatusConta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, String> {

    List<ContaBancaria> findByIdUsuario(String idUsuario);

    List<ContaBancaria> findByStatus(StatusConta status);

    List<ContaBancaria> findByIdUsuarioAndStatus(String idUsuario, StatusConta status);

    boolean existsByBancoIdAndAgenciaAndNumeroConta(String idBanco, String agencia, String numeroConta);

    boolean existsByBancoIdAndAgenciaAndNumeroContaAndIdNot(String idBanco, String agencia, String numeroConta, String id);

    boolean existsByBancoId(String idBanco);
}
