package com.wendelnunes.assembleia.api.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.wendelnunes.assembleia.api.dtos.VotoDTO;
import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.entities.Voto;
import com.wendelnunes.assembleia.domain.services.VotoService;
import com.wendelnunes.assembleia.exceptions.BadRequestException;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.JsonUtil;

@SpringBootTest
@AutoConfigureMockMvc
class VotoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private VotoService votoService;

	private VotoDTO criaVotoDTO(Voto voto) {
		VotoDTO votoDTO = new VotoDTO();
		votoDTO.setCPF(voto.getAssociado().getCPF());
		votoDTO.setIdSessao(voto.getSessao().getId());
		votoDTO.setValor(voto.getValor());
		return votoDTO;
	}

	private Voto criaVoto(Long id, Associado associado, Sessao sessao, boolean valor) {
		Voto voto = new Voto();
		voto.setId(id);
		voto.setAssociado(associado);
		voto.setSessao(sessao);
		voto.setValor(valor);
		return voto;
	}

	private Voto criaVoto() {
		return criaVoto(Long.valueOf(1), criaAssociado(), criaSessao(), false);
	}

	private Associado criaAssociado() {
		Associado associado = new Associado();
		associado.setId(Long.valueOf(1));
		associado.setCPF("87511191029");
		associado.setNome("Associado 1");
		return associado;
	}

	private static Sessao criaSessao() {
		Sessao sessao = new Sessao();
		sessao.setId(Long.valueOf(1));
		sessao.setDescricao("Sessão 1");
		OffsetDateTime now = OffsetDateTime.now();
		sessao.setDataHoraInicio(now);
		sessao.setDataHoraFechamento(now.plusHours(1));
		return sessao;
	}

	@Test
	@DisplayName("Verifica votar com sucesso")
	void verificaVotarComSucesso() throws Exception {
		Voto votoSalvar = criaVoto();
		when(this.votoService.votar(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(votoSalvar);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/votos") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(criaVotoDTO(votoSalvar)))) //
				.andExpect(status().isOk()); //
	}

	@Test
	@DisplayName("Verifica votar sem id da sessão")
	void verificaVotarSemIdSessao() throws Exception {
		Voto votoSalvar = criaVoto();
		votoSalvar.getSessao().setId(null);
		when(this.votoService.votar(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(votoSalvar);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/votos") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(criaVotoDTO(votoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica votar sem CPF")
	void verificaVotarSemCPF() throws Exception {
		Voto votoSalvar = criaVoto();
		votoSalvar.getAssociado().setCPF(null);
		when(this.votoService.votar(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(votoSalvar);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/votos") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(criaVotoDTO(votoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica votar sem valor")
	void verificaVotarSemValor() throws Exception {
		Voto votoSalvar = criaVoto();
		votoSalvar.setValor(null);
		when(this.votoService.votar(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(votoSalvar);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/votos") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(criaVotoDTO(votoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica DateTimeException ao votar")
	void verificaDateTimeExceptionVotar() throws Exception {
		Voto votoSalvar = criaVoto();
		when(this.votoService.votar(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenThrow(new DateTimeException());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/votos") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(criaVotoDTO(votoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica NotFoundException ao votar")
	void verificaNotFoundExceptionVotar() throws Exception {
		Voto votoSalvar = criaVoto();
		when(this.votoService.votar(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenThrow(new NotFoundException());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/votos") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(criaVotoDTO(votoSalvar)))) //
				.andExpect(status().isNotFound()); //
	}

	@Test
	@DisplayName("Verifica BadRequestException ao votar")
	void verificaBadRequestExceptionVotar() throws Exception {
		Voto votoSalvar = criaVoto();
		when(this.votoService.votar(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenThrow(new BadRequestException());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/votos") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(criaVotoDTO(votoSalvar)))) //
				.andExpect(status().isBadRequest()); //
	}

	@Test
	@DisplayName("Verifica ConflictException ao votar")
	void verificaConflictExceptionVotar() throws Exception {
		Voto votoSalvar = criaVoto();
		when(this.votoService.votar(Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenThrow(new ConflictException());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/votos") //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.accept(MediaType.APPLICATION_JSON_VALUE) //
				.content(JsonUtil.toJson(criaVotoDTO(votoSalvar)))) //
				.andExpect(status().isConflict()); //
	}
}