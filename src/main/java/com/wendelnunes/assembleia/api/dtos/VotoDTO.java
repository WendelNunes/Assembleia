package com.wendelnunes.assembleia.api.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Voto")
public class VotoDTO {

	@NotBlank(message = "CPF deve ser informado")
	@CPF(message = "CPF é inválido")
	private String cpf;
	@NotNull(message = "Valor deve ser informado")
	private Boolean valor;
}