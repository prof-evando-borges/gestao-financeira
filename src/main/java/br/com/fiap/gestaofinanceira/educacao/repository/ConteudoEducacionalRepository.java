package br.com.fiap.gestaofinanceira.educacao.repository;

import br.com.fiap.gestaofinanceira.educacao.entity.ConteudoEducacional;
import br.com.fiap.gestaofinanceira.educacao.entity.NivelConhecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConteudoEducacionalRepository extends JpaRepository<ConteudoEducacional, String> {

    // Regra: o titulo nao pode repetir no cadastro
    boolean existsByTituloIgnoreCase(String titulo);

    // Regra: o titulo nao pode repetir na atualizacao (ignorando o proprio conteudo)
    boolean existsByTituloIgnoreCaseAndIdNot(String titulo, String id);

    // GET /api/v1/educacao/conteudos?nivel=INICIANTE
    List<ConteudoEducacional> findByNivel(NivelConhecimento nivel);
}
