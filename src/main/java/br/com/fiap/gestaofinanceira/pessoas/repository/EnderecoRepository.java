package br.com.fiap.gestaofinanceira.pessoas.repository;

import br.com.fiap.gestaofinanceira.pessoas.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnderecoRepository
        extends JpaRepository<Endereco, String> {

    // GET /api/v1/pessoas/enderecos?idUsuario=...
    List<Endereco> findByUsuarioId(String idUsuario);

    // Regra: o usuario nao pode passar do limite de enderecos
    long countByUsuarioId(String idUsuario);

    // Regra: o mesmo usuario nao pode ter dois enderecos com o mesmo CEP e numero
    boolean existsByUsuarioIdAndCepAndNumero(String idUsuario, String cep, String numero);

    boolean existsByUsuarioIdAndCepAndNumeroAndIdNot(String idUsuario, String cep, String numero, String id);
}
