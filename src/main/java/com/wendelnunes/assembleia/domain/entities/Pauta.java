package com.wendelnunes.assembleia.domain.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pauta")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pauta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5151467232691293363L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private Long id;
	@Column(name = "nome")
	private String nome;
	@OneToMany(mappedBy = "pauta")
	private List<Sessao> sessoes;
}