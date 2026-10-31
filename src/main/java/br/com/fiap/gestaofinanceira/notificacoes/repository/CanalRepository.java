package br.com.fiap.gestaofinanceira.notificacoes.repository;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Canal;
import br.com.fiap.gestaofinanceira.notificacoes.entity.NomeCanal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanalRepository extends JpaRepository<Canal, String> {

    // Regra: o nome do canal e unico (UK_NTF_CANAL_NOME)
    boolean existsByNome(NomeCanal nome);

    boolean existsByNomeAndIdNot(NomeCanal nome, String id);

    // GET /api/v1/notificacoes/canais?ativo=true
    List<Canal> findByAtivo(Boolean ativo);
}
