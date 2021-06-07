package com.wendelnunes.assembleia.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "FormularioItem")
public class FormularioItemDTO {

	private TipoFormularioItemDTO tipo;
	private String id;
	private String titulo;
	private Object valor;
}
