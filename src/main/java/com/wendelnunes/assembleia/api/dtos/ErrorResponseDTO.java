package com.wendelnunes.assembleia.api.dtos;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Error")
public class ErrorResponseDTO {

	private String message;
	private String path;
	private OffsetDateTime timestamp;
	private List<String> details;
}