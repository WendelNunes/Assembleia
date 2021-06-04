package com.wendelnunes.assembleia.api.dtos;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Sessao")
public class SessaoDTO {

	private Long id;
	@NotNull(message = "Id da pauta deve ser informado")
	private Long idPauta;
	@NotBlank(message = "Descrição da pauta deve ser informado")
	private String descricao;
	@NotBlank(message = "Data/Hora início deve ser informada")
	private OffsetDateTime dataHoraInicio;
	private OffsetDateTime dataHoraFechamento;
}