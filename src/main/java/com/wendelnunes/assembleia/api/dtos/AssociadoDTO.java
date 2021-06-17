package com.wendelnunes.assembleia.api.dtos;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.br.CPF;
import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wendelnunes.assembleia.domain.entities.Associado;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Associado")
public class AssociadoDTO {

	private Long id;
	@NotBlank(message = "CPF deve ser informado")
	@CPF(message = "CPF é inválido")
	@JsonProperty(value = "CPF")
	private String CPF;
	@NotBlank(message = "Nome deve ser informado")
	private String nome;

	public static Associado toAssociado(AssociadoDTO associadoDTO) {
		return new ModelMapper().map(associadoDTO, Associado.class);
	}

	public static AssociadoDTO from(Associado associado) {
		return new ModelMapper().map(associado, AssociadoDTO.class);
	}
}