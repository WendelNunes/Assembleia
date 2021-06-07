package com.wendelnunes.assembleia.api.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wendelnunes.assembleia.api.dtos.ErrorResponseDTO;
import com.wendelnunes.assembleia.api.dtos.FormularioDTO;
import com.wendelnunes.assembleia.api.dtos.SelecaoDTO;
import com.wendelnunes.assembleia.api.dtos.SessaoDTO;
import com.wendelnunes.assembleia.api.dtos.VotoDTO;
import com.wendelnunes.assembleia.domain.entities.ResultadoSessao;
import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.services.ResultadoSessaoService;
import com.wendelnunes.assembleia.domain.services.SessaoService;
import com.wendelnunes.assembleia.domain.services.VotoService;
import com.wendelnunes.assembleia.exceptions.BadRequestException;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@Api(tags = { "Sessão" })
@RestController
@RequestMapping(value = SessaoController.PATH)
@AllArgsConstructor
public class SessaoController {

	public static final String PATH = "/sessoes";
	private SessaoService sessaoService;
	private VotoService votoService;
	private ResultadoSessaoService resultadoSessaoService;

	@ApiOperation(value = "Abre uma nova sessão para a pauta")
	@ApiResponses(value = { //
			@ApiResponse(code = 201, message = "Sessão aberta com sucesso", response = FormularioDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Pauta inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormularioDTO> abrirSessao(@Valid @RequestBody SessaoDTO sessao)
			throws DateTimeException, NotFoundException {
		Sessao created = this.sessaoService.abrir(SessaoDTO.toSessao(sessao));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest() //
				.path("/{id}") //
				.buildAndExpand(created.getId()) //
				.toUri(); //
		return ResponseEntity.created(uri).body(FormularioDTO.from(created, this.getPath()));
	}

	@ApiOperation(value = "Obtém uma sessão pelo id")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = FormularioDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Sessão inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormularioDTO> obterPorId(@PathVariable("id") Long id) throws NotFoundException {
		return ResponseEntity.ok().body(FormularioDTO.from(this.sessaoService.obterPorId(id), this.getPath()));
	}

	@ApiOperation(value = "Realiza um voto na sessão")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Voto realizado com sucesso", response = void.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Dado não encontrado", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(value = "/{id}/votar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> votar(@PathVariable("id") Long id, @Valid @RequestBody VotoDTO voto)
			throws NotFoundException, DateTimeException, ConflictException, JsonMappingException,
			JsonProcessingException, BadRequestException {
		this.votoService.votar(id, voto.getCpf(), voto.getValor());
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "Obtém todas as sessões")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = SelecaoDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SelecaoDTO> obterTodos() {
		return ResponseEntity.ok().body(SelecaoDTO.fromSessao(this.sessaoService.obterTodos(), this.getPath()));
	}

	@ApiOperation(value = "Obtém o resultado parcial ou final da sessão")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = FormularioDTO.class), //
			@ApiResponse(code = 404, message = "Dado não encontrado", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(value = "/{id}/resultado", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormularioDTO> obterResultado(@PathVariable("id") Long id) throws NotFoundException {
		ResultadoSessao resultadoSessao = this.resultadoSessaoService.obterPorIdSessao(id);
		return ResponseEntity.ok().body(FormularioDTO.from(resultadoSessao, this.getPath()));
	}

	public String getPath() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + PATH;
	}
}