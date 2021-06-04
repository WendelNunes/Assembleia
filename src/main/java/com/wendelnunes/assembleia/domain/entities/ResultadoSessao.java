package com.wendelnunes.assembleia.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "resultado_sessao")
@Immutable
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ResultadoSessao {

	@Id
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private Long id;
	@Column(name = "aberta")
	private Boolean aberta;
	@Column(name = "total_sim")
	private Integer totalSim;
	@Column(name = "total_nao")
	private Integer totalNao;
}