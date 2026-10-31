package br.com.fiap.gestaofinanceira.relatorios.repository;

import br.com.fiap.gestaofinanceira.relatorios.entity.ItemRelatorio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRelatorioRepository extends JpaRepository<ItemRelatorio, String> {
}
