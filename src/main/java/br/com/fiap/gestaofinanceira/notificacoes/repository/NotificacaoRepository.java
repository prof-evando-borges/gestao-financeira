package br.com.fiap.gestaofinanceira.notificacoes.repository;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Notificacao;
import br.com.fiap.gestaofinanceira.notificacoes.entity.StatusNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, String> {

    // GET /api/v1/notificacoes/notificacoes?idPessoa=...&status=...
    List<Notificacao> findByIdPessoa(String idPessoa);

    List<Notificacao> findByStatus(StatusNotificacao status);

    List<Notificacao> findByIdPessoaAndStatus(String idPessoa, StatusNotificacao status);

    // Regra: nao remover canal ou modelo que ja foi usado em notificacoes
    boolean existsByCanalId(String idCanal);

    boolean existsByModeloId(String idModelo);
}
