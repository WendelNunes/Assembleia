package com.wendelnunes.assembleia.api.controllers;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wendelnunes.assembleia.api.dtos.ErrorResponseDTO;
import com.wendelnunes.assembleia.api.dtos.VotoDTO;
import com.wendelnunes.assembleia.domain.services.VotoService;
import com.wendelnunes.assembleia.exceptions.BadRequestException;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/votos")
@AllArgsConstructor
public class VotoController {

	private VotoService votoService;

	@ApiOperation(value = "Realiza um voto")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Voto realizado com sucesso", response = void.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Dado não encontrado", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> votar(@Valid @RequestBody VotoDTO voto) throws NotFoundException, DateTimeException,
			ConflictException, JsonMappingException, JsonProcessingException, BadRequestException {
		this.votoService.votar(voto.getIdSessao(), voto.getCpf(), voto.getValor());
		return ResponseEntity.ok().build();
	}
}