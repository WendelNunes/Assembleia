package com.wendelnunes.assembleia.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.entities.Sessao;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
	boolean findByPauta(Pauta pauta);
}