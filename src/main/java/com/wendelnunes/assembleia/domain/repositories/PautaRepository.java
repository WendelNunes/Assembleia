package com.wendelnunes.assembleia.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wendelnunes.assembleia.domain.entities.Pauta;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
}
