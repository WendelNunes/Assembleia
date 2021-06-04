package com.wendelnunes.assembleia.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wendelnunes.assembleia.domain.entities.Voto;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

	@Query(value = "SELECT COALESCE(COUNT(v),0)>0 FROM Voto v WHERE v.sessao.id = :idSessao AND v.associado.id = :idAssociado")
	boolean existsVotoByIdSessaoAndIdAssociado(@Param("idSessao") Long idSessao,
			@Param("idAssociado") Long idAssociado);
}