package com.wendelnunes.assembleia.api.dtos;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Pauta")
public class PautaDTO {

	private Long id;
	@NotBlank(message = "Nome deve ser informado")
	private String nome;
}