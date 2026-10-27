package br.com.fiap.gestaofinanceira.pessoas.repository;

import br.com.fiap.gestaofinanceira.pessoas.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario, String> {

    // Regra: CPF nao pode repetir no cadastro
    boolean existsByCpf(String cpf);

    // Regra: CPF nao pode repetir na atualizacao (ignorando o proprio usuario)
    boolean existsByCpfAndIdNot(String cpf, String id);

    // GET /api/v1/pessoas/usuarios?cpf=...
    Optional<Usuario> findByCpf(String cpf);

    // GET /api/v1/pessoas/usuarios?nome=...
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}
