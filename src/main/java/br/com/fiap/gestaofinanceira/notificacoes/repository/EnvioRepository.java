package br.com.fiap.gestaofinanceira.notificacoes.repository;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvioRepository extends JpaRepository<Envio, String> {

    // Numero da proxima tentativa e limite de tentativas
    long countByNotificacaoId(String idNotificacao);

    // GET /api/v1/notificacoes/notificacoes/{id}/envios
    List<Envio> findByNotificacaoIdOrderByNumeroTentativaAsc(String idNotificacao);
}
