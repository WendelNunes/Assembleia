package com.wendelnunes.assembleia.api.controllers;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wendelnunes.assembleia.api.dtos.AssociadoDTO;
import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.services.AssociadoService;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.JsonUtil;

@SpringBootTest
@AutoConfigureMockMvc
public class AssociadoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private AssociadoService associadoService;

	private static Associado criaAssociado() {
		return criaAssociado(Long.valueOf(1), "87511191029", "Associado 1");
	}

	private static Associado criaAssociadoDois() {
		return criaAssociado(Long.valueOf(2), "78673472083", "Associado 2");
	}

	private static Associado criaAssociado(Long id, String CPF, String nome) {
		Associado associado = new Associado();
		associado.setId(id);
		associado.setCPF(CPF);
		associado.setNome(nome);
		return associado;
	}

	@Test
	@DisplayName("Verifica criar associado")
	void verificaAssociadoCriadoComSucesso() throws JsonProcessingException, Exception {
		Associado associadoEsperado = criaAssociado();
		Associado associadoSalvar = SerializationUtils.clone(associadoEsperado);
		when(this.associadoService.criar(Mockito.any(Associado.class))).thenReturn(associadoEsperado);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/associados") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isCreated()) //
				.andExpect(MockMvcResultMatchers.content().json(JsonUtil.toJson(AssociadoDTO.from(associadoEsperado)))); //
	}

	@Test
	@DisplayName("Verifica criar associado sem CPF")
	void verificaCriarAssociadoSemCPF() throws JsonProcessingException, Exception {
		Associado associadoSalvar = criaAssociado();
		associadoSalvar.setCPF(null);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/associados") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica criar associado com CPF inválido")
	void verificaCriarAssociadoComCPFInvalido() throws JsonProcessingException, Exception {
		Associado associadoSalvar = criaAssociado();
		associadoSalvar.setCPF("12345678910");
		this.mockMvc.perform(MockMvcRequestBuilders.post("/associados") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica criar associado sem nome")
	void verificaCriarAssociadoSemNome() throws JsonProcessingException, Exception {
		Associado associadoSalvar = criaAssociado();
		associadoSalvar.setNome(null);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/associados") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica ConflictException ao criar associado")
	void verificaConflictExceptionCriarAssociado() throws JsonProcessingException, Exception {
		Associado associadoSalvar = criaAssociado();
		when(this.associadoService.criar(Mockito.any(Associado.class))).thenThrow(new ConflictException());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/associados") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isConflict()); //
	}

	@Test
	@DisplayName("Verifica atualizar associado")
	void verificaAssociadoAtualizarComSucesso() throws JsonProcessingException, Exception {
		Associado associadoEsperado = criaAssociado();
		associadoEsperado.setNome("Associado Alterado");
		Associado associadoSalvar = SerializationUtils.clone(associadoEsperado);
		when(this.associadoService.atualizar(Mockito.any(Associado.class))).thenReturn(associadoEsperado);
		this.mockMvc.perform(MockMvcRequestBuilders.put("/associados/" + associadoSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content().json(JsonUtil.toJson(AssociadoDTO.from(associadoEsperado)))); //
	}

	@Test
	@DisplayName("Verifica atualizar associado sem CPF")
	void verificaAtualizarAssociadoSemCPF() throws JsonProcessingException, Exception {
		Associado associadoSalvar = criaAssociado();
		associadoSalvar.setCPF(null);
		this.mockMvc.perform(MockMvcRequestBuilders.put("/associados/" + associadoSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica atualizar associado com CPF inválido")
	void verificaAtualizarAssociadoComCPFInvalido() throws JsonProcessingException, Exception {
		Associado associadoSalvar = criaAssociado();
		associadoSalvar.setCPF("12345678910");
		this.mockMvc.perform(MockMvcRequestBuilders.put("/associados/" + associadoSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica atualizar associado sem nome")
	void verificaAtualizarAssociadoSemNome() throws JsonProcessingException, Exception {
		Associado associadoSalvar = criaAssociado();
		associadoSalvar.setNome(null);
		this.mockMvc.perform(MockMvcRequestBuilders.put("/associados/" + associadoSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica NotFoundException ao atualizar associado")
	void verificaNotFoundExceptionAtualizarAssociado() throws JsonProcessingException, Exception {
		Associado associadoEsperado = criaAssociado();
		associadoEsperado.setNome("Associado Alterado");
		Associado associadoSalvar = SerializationUtils.clone(associadoEsperado);
		when(this.associadoService.atualizar(Mockito.any(Associado.class))).thenThrow(new NotFoundException());
		this.mockMvc.perform(MockMvcRequestBuilders.put("/associados/" + associadoSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isNotFound()); //
	}

	@Test
	@DisplayName("Verifica ConflictException ao atualizar associado")
	void verificaConflictExceptionAtualizarAssociado() throws JsonProcessingException, Exception {
		Associado associadoEsperado = criaAssociado();
		associadoEsperado.setNome("Associado Alterado");
		Associado associadoSalvar = SerializationUtils.clone(associadoEsperado);
		when(this.associadoService.atualizar(Mockito.any(Associado.class))).thenThrow(new ConflictException());
		this.mockMvc.perform(MockMvcRequestBuilders.put("/associados/" + associadoSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(AssociadoDTO.from(associadoSalvar)))) //
				.andExpect(status().isConflict()); //
	}

	@Test
	@DisplayName("Verifica deletar associado")
	void verificaDeletarAssociado() throws JsonProcessingException, Exception {
		doNothing().when(this.associadoService).deletar(Mockito.anyLong());
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/associados/" + 1) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()); //
	}

	@Test
	@DisplayName("Verifica NotFoundException ao deletar associado")
	void verificaNotFoundExceptionDeletarAssociado() throws JsonProcessingException, Exception {
		doThrow(new NotFoundException()).when(this.associadoService).deletar(Mockito.anyLong());
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/associados/" + 1) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isNotFound()); //
	}

	@Test
	@DisplayName("Verifica obter associado")
	void verificaObterAssociado() throws JsonProcessingException, Exception {
		Associado associadoEsperado = criaAssociado();
		when(this.associadoService.obterPorId(Mockito.anyLong())).thenReturn(associadoEsperado);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/associados/" + 1) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content().json(JsonUtil.toJson(AssociadoDTO.from(associadoEsperado)))); //
	}

	@Test
	@DisplayName("Verifica NotFoundException obter associado")
	void verificaNotFoundExceptionObterAssociado() throws JsonProcessingException, Exception {
		when(this.associadoService.obterPorId(Mockito.anyLong())).thenThrow(new NotFoundException());
		this.mockMvc.perform(MockMvcRequestBuilders.get("/associados/" + 1) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isNotFound()); //
	}

	@Test
	@DisplayName("Verifica obter todos associados")
	void verificaObterTodosAssociados() throws JsonProcessingException, Exception {
		Associado associado1 = criaAssociado();
		Associado associado2 = criaAssociadoDois();
		List<Associado> listaEsperada = asList(associado1, associado2);
		when(this.associadoService.obterTodos(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()))
				.thenReturn(new PageImpl<Associado>(listaEsperada));
		this.mockMvc.perform(MockMvcRequestBuilders.get("/associados") //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content().json(
						JsonUtil.toJson(listaEsperada.stream().map(AssociadoDTO::from).collect(Collectors.toList())))); //
	}
}
