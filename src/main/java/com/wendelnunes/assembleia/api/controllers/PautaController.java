package com.wendelnunes.assembleia.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.wendelnunes.assembleia.api.dtos.ErrorResponseDTO;
import com.wendelnunes.assembleia.api.dtos.PautaDTO;
import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.services.PautaService;
import com.wendelnunes.assembleia.exceptions.NotDeleteException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = { "Pauta" })
@RestController
@RequestMapping("/pautas")
@AllArgsConstructor
@Slf4j
public class PautaController {

	public PautaService pautaService;

	@ApiOperation(value = "Cria uma nova pauta")
	@ApiResponses(value = { //
			@ApiResponse(code = 201, message = "Cadastro realizada com sucesso", response = PautaDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PautaDTO> criar(@Valid @RequestBody PautaDTO pauta) {
		log.debug("REST requisição para salvar pauta: {}", pauta);
		Pauta pautaSalvo = this.pautaService.criar(PautaDTO.toPauta(pauta));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest() //
				.path("/{id}") //
				.buildAndExpand(pautaSalvo.getId()) //
				.toUri(); //
		return ResponseEntity.created(uri).body(PautaDTO.from(pautaSalvo));
	}

	@ApiOperation(value = "Atualiza uma pauta")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Cadastro atualizado com sucesso", response = PautaDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Pauta inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PautaDTO> atualizar(@PathVariable("id") Long id, @Valid @RequestBody PautaDTO pauta)
			throws NotFoundException {
		log.debug("REST requisição para atualizar pauta({}): {}", id, pauta);
		Pauta p = PautaDTO.toPauta(pauta);
		p.setId(id);
		Pauta updated = this.pautaService.atualizar(p);
		return ResponseEntity.ok(PautaDTO.from(updated));
	}

	@ApiOperation(value = "Deleta uma pauta")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Deleção realizada com sucesso", response = void.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Pauta inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id) throws NotFoundException, NotDeleteException {
		log.debug("REST requisição para deletar pauta: {}", id);
		this.pautaService.deletar(id);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "Obtém uma pauta por id")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = PautaDTO.class), //
			@ApiResponse(code = 400, message = "Requisição mal formada", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 404, message = "Pauta inexistente", response = ErrorResponseDTO.class), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PautaDTO> obterPorId(@PathVariable("id") Long id) throws NotFoundException {
		log.debug("REST requisição para obter pauta: {}", id);
		return ResponseEntity.ok().body(PautaDTO.from(this.pautaService.obterPorId(id)));
	}

	@ApiOperation(value = "Obtém todas as pautas")
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Requisição realizada com sucesso", response = PautaDTO.class, responseContainer = "List"), //
			@ApiResponse(code = 500, message = "Erro interno do servidor", response = ErrorResponseDTO.class), //
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PautaDTO>> obterTodos(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "id") String[] sortBy) {
		log.debug("REST requisição para obter todas pautas: Page {}, pageSize {}, sortBy {}", page, pageSize, sortBy);
		Page<Pauta> pageResult = this.pautaService.obterTodos(page, pageSize, sortBy);
		return ResponseEntity.ok()
				.body(pageResult.getContent().stream().map(PautaDTO::from).collect(Collectors.toList()));
	}
}