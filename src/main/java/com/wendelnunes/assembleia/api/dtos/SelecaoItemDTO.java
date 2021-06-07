package com.wendelnunes.assembleia.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "SelecaoItem")
public class SelecaoItemDTO {

	private String texto;
	private String url;
}
