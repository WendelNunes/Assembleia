package com.wendelnunes.assembleia.api.controllers;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
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
import com.wendelnunes.assembleia.api.dtos.ResultadoSessaoDTO;
import com.wendelnunes.assembleia.api.dtos.SessaoDTO;
import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.entities.ResultadoSessao;
import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.services.SessaoService;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.JsonUtil;

@SpringBootTest
@AutoConfigureMockMvc
class SessaoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private SessaoService sessaoService;

	private static Sessao criaSessao(Long id, String descricao, OffsetDateTime dataHoraInicio,
			OffsetDateTime dataHoraFinal) {
		Sessao sessao = new Sessao();
		sessao.setId(id);
		sessao.setDescricao(descricao);
		sessao.setDataHoraInicio(dataHoraInicio);
		sessao.setDataHoraFechamento(dataHoraFinal);
		Pauta pauta = new Pauta();
		pauta.setId(id);
		pauta.setNome("Pauta");
		sessao.setPauta(pauta);
		return sessao;
	}

	private Sessao criaSessao() {
		OffsetDateTime now = OffsetDateTime.now();
		return criaSessao(Long.valueOf(1), "Sessão 1", now, now.plusHours(1));
	}

	private Sessao criaSessaoDois() {
		OffsetDateTime now = OffsetDateTime.now();
		return criaSessao(Long.valueOf(2), "Sessão 2", now, now.plusHours(1));
	}

	@Test
	@DisplayName("Verifica abrir sessao")
	void verificaSessaoAbertaComSucesso() throws JsonProcessingException, Exception {
		Sessao sessaoEsperada = criaSessao();
		Sessao sessaoSalvar = SerializationUtils.clone(sessaoEsperada);
		when(this.sessaoService.abrir(Mockito.any(Sessao.class))).thenReturn(sessaoEsperada);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/sessoes") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(SessaoDTO.from(sessaoSalvar)))) //
				.andExpect(status().isCreated()) //
				.andExpect(MockMvcResultMatchers.content().json(JsonUtil.toJson(SessaoDTO.from(sessaoEsperada)))); //
	}

	@Test
	@DisplayName("Verifica criar sessao sem id da pauta")
	void verificaCriarSessaoSemIdPauta() throws JsonProcessingException, Exception {
		Sessao sessaoSalvar = criaSessao();
		sessaoSalvar.setPauta(null);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/sessoes") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(SessaoDTO.from(sessaoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica criar sessao sem descricao")
	void verificaCriarSessaoSemNome() throws JsonProcessingException, Exception {
		Sessao sessaoSalvar = criaSessao();
		sessaoSalvar.setDescricao(null);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/sessoes") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(SessaoDTO.from(sessaoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica criar sessao sem data/hora inicio")
	void verificaCriarSessaoSemDataHoraInicio() throws JsonProcessingException, Exception {
		Sessao sessaoSalvar = criaSessao();
		sessaoSalvar.setDataHoraInicio(null);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/sessoes") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(SessaoDTO.from(sessaoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica NotFoundException ao criar sessao")
	void verificaNotFoundExceptionCriarSessao() throws JsonProcessingException, Exception {
		Sessao sessaoSalvar = criaSessao();
		when(this.sessaoService.abrir(Mockito.any(Sessao.class))).thenThrow(new NotFoundException());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/sessoes") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(SessaoDTO.from(sessaoSalvar)))) //
				.andExpect(status().isNotFound()); //
	}

	@Test
	@DisplayName("Verifica DateTimeException ao criar sessao")
	void verificaDateTimeExceptionCriarSessao() throws JsonProcessingException, Exception {
		Sessao sessaoSalvar = criaSessao();
		when(this.sessaoService.abrir(Mockito.any(Sessao.class))).thenThrow(new DateTimeException());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/sessoes") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(SessaoDTO.from(sessaoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica obter sessao")
	void verificaObterSessao() throws JsonProcessingException, Exception {
		Sessao sessaoEsperada = criaSessao();
		when(this.sessaoService.obterPorId(Mockito.anyLong())).thenReturn(sessaoEsperada);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/sessoes/" + 1) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content().json(JsonUtil.toJson(SessaoDTO.from(sessaoEsperada)))); //
	}

	@Test
	@DisplayName("Verifica obter sessao inexistente")
	void verificaObterSessaoInexistente() throws JsonProcessingException, Exception {
		when(this.sessaoService.obterPorId(Mockito.anyLong())).thenThrow(new NotFoundException(""));
		this.mockMvc.perform(MockMvcRequestBuilders.get("/sessoes/" + 1) //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isNotFound()); //
	}

	@Test
	@DisplayName("Verifica obter todas sessoes")
	void verificaObterTodosSessoes() throws JsonProcessingException, Exception {
		Sessao sessao1 = criaSessao();
		Sessao sessao2 = criaSessaoDois();
		List<Sessao> listaEsperada = asList(sessao1, sessao2);
		when(this.sessaoService.obterTodos(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()))
				.thenReturn(new PageImpl<Sessao>(listaEsperada));
		this.mockMvc.perform(MockMvcRequestBuilders.get("/sessoes") //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content().json(
						JsonUtil.toJson(listaEsperada.stream().map(SessaoDTO::from).collect(Collectors.toList())))); //
	}

	@Test
	@DisplayName("Verifica obter resultado da sessao")
	void verificaObterResultadoSessao() throws JsonProcessingException, Exception {
		ResultadoSessao resultadoSessao = new ResultadoSessao(Long.valueOf(1), true, 5, 10);
		when(this.sessaoService.obterResultadoPorIdSessao(Mockito.anyLong())).thenReturn(resultadoSessao);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/sessoes/" + 1 + "/resultado") //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isOk()) //
				.andExpect(MockMvcResultMatchers.content()
						.json(JsonUtil.toJson(ResultadoSessaoDTO.from(resultadoSessao)))); //
	}

	@Test
	@DisplayName("Verifica NotFoundException ao obter resultado da sessao")
	void verificaNotFoundExceptionObterResultadoSessao() throws JsonProcessingException, Exception {
		when(this.sessaoService.obterResultadoPorIdSessao(Mockito.anyLong())).thenThrow(new NotFoundException());
		this.mockMvc.perform(MockMvcRequestBuilders.get("/sessoes/" + 1 + "/resultado") //
				.accept(MediaType.APPLICATION_JSON_VALUE)) //
				.andExpect(status().isNotFound()); //
	}
}