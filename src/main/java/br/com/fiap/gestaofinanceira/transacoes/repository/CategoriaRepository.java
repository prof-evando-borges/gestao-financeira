package br.com.fiap.gestaofinanceira.transacoes.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.gestaofinanceira.transacoes.entity.Categoria;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, String> {

    Optional<Categoria> findByNome(String nome);
}
