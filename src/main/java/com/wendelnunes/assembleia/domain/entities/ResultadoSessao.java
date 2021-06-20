package com.wendelnunes.assembleia.domain.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ResultadoSessao {

	@EqualsAndHashCode.Include
	private Long idSessao;
	private Boolean aberta;
	private Integer totalSim;
	private Integer totalNao;
}