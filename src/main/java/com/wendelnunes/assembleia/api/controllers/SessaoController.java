package com.wendelnunes.assembleia.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.wendelnunes.assembleia.api.dtos.ErrorResponseDTO;
import com.wendelnunes.assembleia.api.dtos.ResultadoSessaoDTO;
import com.wendelnunes.assembleia.api.dtos.SessaoDTO;
import com.wendelnunes.assembleia.domain.entities.ResultadoSessao;
import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.services.SessaoService;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = { "Sessão" })
@RestController
@RequestMapping("/sessoes")
@AllArgsConstructor
@Slf4j
public class SessaoController {

	private SessaoService sessaoService;

	@ApiOperation(value = "Abre uma nova sessão para a pauta")
	@ApiResponses(value = { //
			@ApiResponse(code = 201, message = "Sessão aberta com sucesso", response = SessaoDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Pauta inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SessaoDTO> abrirSessao(@Valid @RequestBody SessaoDTO sessao)
			throws DateTimeException, NotFoundException {
		log.debug("REST requisição para salvar sessão: {}", sessao);
		Sessao created = this.sessaoService.abrir(SessaoDTO.toSessao(sessao));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest() //
				.path("/{id}") //
				.buildAndExpand(created.getId()) //
				.toUri(); //
		return ResponseEntity.created(uri).body(SessaoDTO.from(created));
	}

	@ApiOperation(value = "Obtém uma sessão pelo id")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = SessaoDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Sessão inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SessaoDTO> obterPorId(@PathVariable("id") Long id) throws NotFoundException {
		log.debug("REST requisição para obter sessão: {}", id);
		return ResponseEntity.ok().body(SessaoDTO.from(this.sessaoService.obterPorId(id)));
	}

	@ApiOperation(value = "Obtém todas as sessões")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = SessaoDTO.class, responseContainer = "List"), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SessaoDTO>> obterTodos(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "id") String[] sortBy) {
		log.debug("REST requisição para obter todas sessões: Page {}, pageSize {}, sortBy {}", page, pageSize, sortBy);
		Page<Sessao> pageResult = this.sessaoService.obterTodos(page, pageSize, sortBy);
		return ResponseEntity.ok()
				.body(pageResult.getContent().stream().map(SessaoDTO::from).collect(Collectors.toList()));
	}

	@ApiOperation(value = "Obtém o resultado parcial ou final da sessão")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = ResultadoSessaoDTO.class), //
			@ApiResponse(code = 404, message = "Dado não encontrado", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@GetMapping(value = "/{id}/resultado", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResultadoSessaoDTO> obterResultado(@PathVariable("id") Long id) throws NotFoundException {
		log.debug("REST requisição para obter resultado da sessão: {}", id);
		ResultadoSessao resultadoSessao = this.sessaoService.obterResultadoPorIdSessao(id);
		return ResponseEntity.ok().body(ResultadoSessaoDTO.from(resultadoSessao));
	}
}