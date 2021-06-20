package com.wendelnunes.assembleia.domain.entities;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "sessao")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sessao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8923906583131807560L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private Long id;
	@ManyToOne
	@JoinColumn(name = "id_pauta")
	private Pauta pauta;
	@Column(name = "descricao")
	private String descricao;
	@Column(name = "data_hora_inicio")
	private OffsetDateTime dataHoraInicio;
	@Column(name = "data_hora_fechamento")
	private OffsetDateTime dataHoraFechamento;

	public boolean isAberta() {
		OffsetDateTime now = OffsetDateTime.now();
		return now.compareTo(this.dataHoraInicio) >= 0 && now.compareTo(this.dataHoraFechamento) <= 0;
	}
}