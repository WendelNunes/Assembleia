package com.wendelnunes.assembleia.api.controllers;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import com.wendelnunes.assembleia.api.dtos.PautaDTO;
import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.services.PautaService;
import com.wendelnunes.assembleia.exceptions.NotDeleteException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.JsonUtil;

@SpringBootTest
@AutoConfigureMockMvc
class PautaControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private PautaService pautaService;

	private static Pauta criaPauta(Long id, String nome) {
		Pauta pauta = new Pauta();
		pauta.setId(Long.valueOf(id));
		pauta.setNome(nome);
		pauta.setSessoes(new ArrayList<>());
		return pauta;
	}

	private static Pauta criaPauta() {
		return criaPauta(Long.valueOf(1), "Pauta 1");
	}

	private static Pauta criaPautaDois() {
		return criaPauta(Long.valueOf(2), "Pauta 2");
	}

	@Test
	@DisplayName("Verifica criar pauta")
	void verificaPautaCriadoComSucesso() throws JsonProcessingException, Exception {
		Pauta pautaEsperada = criaPauta();
		Pauta pautaSalvar = SerializationUtils.clone(pautaEsperada);
		when(this.pautaService.criar(Mockito.any(Pauta.class))).thenReturn(pautaEsperada);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/pautas") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(PautaDTO.from(pautaSalvar)))) //
				.andExpect(status().isCreated()) //
				.andExpect(MockMvcResultMatchers.content().json(JsonUtil.toJson(PautaDTO.from(pautaEsperada)))); //
	}

	@Test
	@DisplayName("Verifica criar pauta sem nome")
	void verificaCriarPautaSemNome() throws JsonProcessingException, Exception {
		Pauta pautaSalvar = criaPauta();
		pautaSalvar.setNome(null);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/pautas") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(PautaDTO.from(pautaSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica atualizar pauta")
	void verificaPautaAtualizarComSucesso() throws JsonProcessingException, Exception {
		Pauta pautaEsperada = criaPauta();
		pautaEsperada.setNome("Pauta Alterada");
		Pauta pautaSalvar = SerializationUtils.clone(pautaEsperada);
		when(this.pautaService.atualizar(Mockito.any(Pauta.class))).thenReturn(pautaEsperada);
		this.mockMvc.perform(MockMvcRequestBuilders.put("/pautas/" + pautaSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(PautaDTO.from(pautaSalvar)))) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content().json(JsonUtil.toJson(PautaDTO.from(pautaEsperada)))); //
	}

	@Test
	@DisplayName("Verifica atualizar pauta sem nome")
	void verificaAtualizarPautaSemNome() throws JsonProcessingException, Exception {
		Pauta pautaSalvar = criaPauta();
		pautaSalvar.setNome(null);
		this.mockMvc.perform(MockMvcRequestBuilders.put("/pautas/" + pautaSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(PautaDTO.from(pautaSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica NotFoundException ao atualizar pauta")
	void verificaNotFoundExceptionAtualizarPauta() throws JsonProcessingException, Exception {
		Pauta pautaSalvar = criaPauta();
		when(this.pautaService.atualizar(Mockito.any(Pauta.class))).thenThrow(new NotFoundException());
		this.mockMvc.perform(MockMvcRequestBuilders.put("/pautas/" + pautaSalvar.getId()) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(PautaDTO.from(pautaSalvar)))) //
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Verifica deletar pauta")
	void verificaDeletarPauta() throws JsonProcessingException, Exception {
		doNothing().when(this.pautaService).deletar(Mockito.anyLong());
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/pautas/" + 1) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()); //
	}

	@Test
	@DisplayName("Verifica NotFoundException deletar pauta")
	void verificaNotFoundExceptionDeletarPauta() throws JsonProcessingException, Exception {
		doThrow(new NotFoundException()).when(this.pautaService).deletar(Mockito.anyLong());
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/pautas/" + 1) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isNotFound()); //
	}

	@Test
	@DisplayName("Verifica NotDeleteException deletar pauta")
	void verificaNotDeleteExceptionDeletarPauta() throws JsonProcessingException, Exception {
		doThrow(new NotDeleteException()).when(this.pautaService).deletar(Mockito.anyLong());
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/pautas/" + 1) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica obter pauta")
	void verificaObterPauta() throws JsonProcessingException, Exception {
		Pauta pautaEsperada = criaPauta();
		when(this.pautaService.obterPorId(Mockito.anyLong())).thenReturn(pautaEsperada);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/pautas/" + 1) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content().json(JsonUtil.toJson(PautaDTO.from(pautaEsperada)))); //
	}

	@Test
	@DisplayName("Verifica NotFoundException ao obter pauta")
	void verificaNotFoundExceptionObterPauta() throws JsonProcessingException, Exception {
		when(this.pautaService.obterPorId(Mockito.anyLong())).thenThrow(new NotFoundException());
		this.mockMvc.perform(MockMvcRequestBuilders.get("/pautas/" + 1) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isNotFound()); //
	}

	@Test
	@DisplayName("Verifica obter todas pautas")
	void verificaObterTodosPautas() throws JsonProcessingException, Exception {
		Pauta pauta1 = criaPauta();
		Pauta pauta2 = criaPautaDois();
		List<Pauta> listaEsperada = asList(pauta1, pauta2);
		when(this.pautaService.obterTodos(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()))
				.thenReturn(new PageImpl<Pauta>(listaEsperada));
		this.mockMvc.perform(MockMvcRequestBuilders.get("/pautas") //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content().json(
						JsonUtil.toJson(listaEsperada.stream().map(PautaDTO::from).collect(Collectors.toList())))); //
	}
}