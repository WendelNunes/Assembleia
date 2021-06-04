package com.wendelnunes.assembleia.api.dtos;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Voto")
public class VotoDTO {

	@JsonProperty(access = Access.READ_ONLY)
	private Long id;
	private Long idSessao;
	@NotNull(message = "Id do associado deve ser informado")
	private Long idAssociado;
	@NotNull(message = "Valor deve ser informado")
	private Boolean valor;
}
