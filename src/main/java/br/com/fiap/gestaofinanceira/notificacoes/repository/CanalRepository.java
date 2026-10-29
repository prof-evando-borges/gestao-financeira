package br.com.fiap.gestaofinanceira.notificacoes.repository;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Canal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanalRepository extends JpaRepository<Canal, String> {
}
