package com.wendelnunes.assembleia.api.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Voto")
public class VotoDTO {

	@NotNull(message = "Id da sessão deve ser informada")
	private Long idSessao;
	@NotBlank(message = "CPF deve ser informado")
	@CPF(message = "CPF é inválido")
	@JsonProperty(value = "cpf")
	private String CPF;
	@NotNull(message = "Valor deve ser informado")
	private Boolean valor;
}