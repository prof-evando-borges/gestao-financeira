package br.com.fiap.gestaofinanceira.educacao.repository;

import br.com.fiap.gestaofinanceira.educacao.entity.ProgressoUsuario;
import br.com.fiap.gestaofinanceira.educacao.entity.StatusProgresso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressoUsuarioRepository extends JpaRepository<ProgressoUsuario, String> {

    // Regra: um usuario tem no maximo um progresso por conteudo
    boolean existsByIdUsuarioAndConteudoId(String idUsuario, String idConteudo);

    // Regra: nao remover conteudo que ja tem progresso de algum usuario
    boolean existsByConteudoId(String idConteudo);

    // GET /api/v1/educacao/progressos?idUsuario=...
    List<ProgressoUsuario> findByIdUsuario(String idUsuario);

    // GET /api/v1/educacao/progressos?status=CONCLUIDO
    List<ProgressoUsuario> findByStatus(StatusProgresso status);
}
