package com.wendelnunes.assembleia.api.dtos;

import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.wendelnunes.assembleia.domain.entities.ResultadoSessao;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "ResultadoSessao")
public class ResultadoSessaoDTO {

	@JsonProperty(access = Access.READ_ONLY)
	private Long idSessao;
	@JsonProperty(access = Access.READ_ONLY)
	private Boolean aberta;
	@JsonProperty(access = Access.READ_ONLY)
	private Integer totalSim;
	@JsonProperty(access = Access.READ_ONLY)
	private Integer totalNao;

	public static ResultadoSessaoDTO from(ResultadoSessao resultadoSessao) {
		return new ModelMapper().map(resultadoSessao, ResultadoSessaoDTO.class);
	}
}