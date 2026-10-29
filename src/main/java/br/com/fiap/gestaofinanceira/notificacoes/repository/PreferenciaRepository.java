package br.com.fiap.gestaofinanceira.notificacoes.repository;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Preferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenciaRepository extends JpaRepository<Preferencia, String> {
}
