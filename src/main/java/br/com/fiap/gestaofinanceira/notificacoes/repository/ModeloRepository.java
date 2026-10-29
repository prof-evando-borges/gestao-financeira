package br.com.fiap.gestaofinanceira.notificacoes.repository;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloRepository extends JpaRepository<Modelo, String> {
}
