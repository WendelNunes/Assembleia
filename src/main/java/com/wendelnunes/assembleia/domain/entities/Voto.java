package com.wendelnunes.assembleia.domain.entities;

import java.io.Serializable;

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
@Table(name = "voto")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Voto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4789684055963583054L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private Long id;
	@ManyToOne
	@JoinColumn(name = "id_sessao")
	private Sessao sessao;
	@ManyToOne
	@JoinColumn(name = "id_associado")
	private Associado associado;
	@Column(name = "valor")
	private Boolean valor;
}