package com.wendelnunes.assembleia.domain.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "associado")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Associado implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4336638914654770993L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private Long id;
	@Column(name = "cpf")
	private String CPF;
	@Column(name = "nome")
	private String nome;
}