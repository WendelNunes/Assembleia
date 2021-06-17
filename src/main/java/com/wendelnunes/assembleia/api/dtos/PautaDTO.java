package com.wendelnunes.assembleia.api.dtos;

import javax.validation.constraints.NotBlank;

import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wendelnunes.assembleia.domain.entities.Pauta;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Pauta")
public class PautaDTO {

	private Long id;
	@NotBlank(message = "Nome deve ser informado")
	private String nome;

	public static Pauta toPauta(PautaDTO pautaDTO) {
		return new ModelMapper().map(pautaDTO, Pauta.class);
	}

	public static PautaDTO from(Pauta pauta) {
		return new ModelMapper().map(pauta, PautaDTO.class);
	}
}