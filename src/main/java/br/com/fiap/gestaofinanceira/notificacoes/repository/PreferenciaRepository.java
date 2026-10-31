package br.com.fiap.gestaofinanceira.notificacoes.repository;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Preferencia;
import br.com.fiap.gestaofinanceira.notificacoes.entity.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferenciaRepository extends JpaRepository<Preferencia, String> {

    // Regra: uma pessoa tem no maximo uma preferencia por canal e tipo de evento (UK_NTF_PREFERENCIA_EVENTO)
    boolean existsByIdPessoaAndCanalIdAndTipoEvento(String idPessoa, String idCanal, TipoEvento tipoEvento);

    boolean existsByIdPessoaAndCanalIdAndTipoEventoAndIdNot(String idPessoa, String idCanal,
                                                           TipoEvento tipoEvento, String id);

    // Regra: nao notificar por um canal/evento que a pessoa desabilitou
    Optional<Preferencia> findByIdPessoaAndCanalIdAndTipoEvento(String idPessoa, String idCanal, TipoEvento tipoEvento);

    // Regra: nao remover canal que ainda tem preferencias
    boolean existsByCanalId(String idCanal);

    // GET /api/v1/notificacoes/preferencias?idPessoa=...
    List<Preferencia> findByIdPessoa(String idPessoa);
}
