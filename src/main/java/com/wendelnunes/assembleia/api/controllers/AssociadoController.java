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

import com.wendelnunes.assembleia.api.dtos.AssociadoDTO;
import com.wendelnunes.assembleia.api.dtos.ErrorResponseDTO;
import com.wendelnunes.assembleia.api.dtos.FormularioDTO;
import com.wendelnunes.assembleia.api.dtos.SelecaoDTO;
import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.services.AssociadoService;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@Api(tags = { "Associado" })
@RestController
@RequestMapping(value = AssociadoController.PATH)
@AllArgsConstructor
public class AssociadoController {

	public static final String PATH = "/associados";
	public AssociadoService associadoService;

	@ApiOperation(value = "Cria um novo associado")
	@ApiResponses(value = { //
			@ApiResponse(code = 201, message = "Cadastro real3izada com sucesso", response = AssociadoDTO.class), //
			@ApiResponse(code = 409, message = "CPF já cadastrado", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormularioDTO> criar(@Valid @RequestBody AssociadoDTO associado) throws ConflictException {
		Associado associadoSalvo = this.associadoService.criar(AssociadoDTO.toAssociado(associado));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest() //
				.path("/{id}") //
				.buildAndExpand(associadoSalvo.getId()) //
				.toUri(); //
		return ResponseEntity.created(uri).body(FormularioDTO.from(associadoSalvo, this.getPath()));
	}

	@ApiOperation(value = "Atualiza um associado")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Cadastro atualizado com sucesso", response = AssociadoDTO.class), //
			@ApiResponse(code = 409, message = "CPF já cadastrado", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Associado inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormularioDTO> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody AssociadoDTO associado) throws ConflictException, NotFoundException {
		Associado a = AssociadoDTO.toAssociado(associado);
		a.setId(id);
		Associado updated = this.associadoService.atualizar(a);
		return ResponseEntity.ok(FormularioDTO.from(updated, this.getPath()));
	}

	@ApiOperation(value = "Deleta um associado")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Deleção realizada com sucesso", response = void.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Associado inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deletar(@PathVariable("id") Long id) throws NotFoundException {
		this.associadoService.deletar(id);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "Obtém um associado por id")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = FormularioDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Associado inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormularioDTO> obterPorId(@PathVariable("id") Long id) throws NotFoundException {
		return ResponseEntity.ok().body(FormularioDTO.from(this.associadoService.obterPorId(id), this.getPath()));
	}

	@ApiOperation(value = "Obtém todos os associados")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = SelecaoDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SelecaoDTO> obterTodos() {
		return ResponseEntity.ok().body(SelecaoDTO.fromAssociado(this.associadoService.obterTodos(), this.getPath()));
	}

	public String getPath() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + PATH;
	}
}