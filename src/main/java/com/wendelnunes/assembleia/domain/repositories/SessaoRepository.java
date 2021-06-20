package com.wendelnunes.assembleia.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.entities.Sessao;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
	boolean findByPauta(Pauta pauta);

	@Query("" //
			+ "   SELECT SUM(CASE WHEN v.valor = TRUE THEN 1 ELSE 0 END) AS totalSim,\n" //
			+ "          SUM(CASE WHEN v.valor = FALSE THEN 1 ELSE 0 END) AS totalNao\n" //
			+ "     FROM Voto v\n" //
			+ "    WHERE v.sessao.id = :idSessao")
	List<Object[]> obterResultadoPorIdSessao(@Param("idSessao") Long idSessao);
}