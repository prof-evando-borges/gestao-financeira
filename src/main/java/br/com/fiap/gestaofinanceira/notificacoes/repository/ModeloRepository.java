package br.com.fiap.gestaofinanceira.notificacoes.repository;

import br.com.fiap.gestaofinanceira.notificacoes.entity.Modelo;
import br.com.fiap.gestaofinanceira.notificacoes.entity.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeloRepository extends JpaRepository<Modelo, String> {

    // Regra: o codigo do modelo e unico (UK_NTF_MODELO_CODIGO)
    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, String id);

    // GET /api/v1/notificacoes/modelos?tipoEvento=PARCELA_DIVIDA
    List<Modelo> findByTipoEvento(TipoEvento tipoEvento);
}
