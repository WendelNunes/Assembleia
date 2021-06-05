package com.wendelnunes.assembleia.domain.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.entities.Voto;
import com.wendelnunes.assembleia.domain.repositories.VotoRepository;
import com.wendelnunes.assembleia.exceptions.BadRequestException;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.DateTimeUtil;

@SpringBootTest
class VotoServiceTest {

	private static String ABLE_TO_VOTE = "{\"status\": \"ABLE_TO_VOTE\"}";
	private static String UNABLE_TO_VOTE = "{\"status\": \"UNABLE_TO_VOTE\"}";

	@Mock
	private VotoRepository votoRepository;
	@Mock
	private SessaoService sessaoService;
	@Mock
	private AssociadoService associadoService;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private DateTimeUtil dateTimeUtil;
	@InjectMocks
	private VotoService votoService;

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
	@DisplayName("Verifica votar")
	void votar() throws NotFoundException, DateTimeException, ConflictException, JsonMappingException,
			JsonProcessingException, BadRequestException {
		Voto voto = criaVoto();
		doReturn(voto.getSessao().getDataHoraInicio()).when(this.dateTimeUtil).currentDateTime();
		doReturn(voto.getSessao()).when(this.sessaoService).obterPorId(Mockito.anyLong());
		doReturn(Optional.of(voto.getAssociado())).when(this.associadoService).obterPorCPF(Mockito.anyString());
		when(this.restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class), Mockito.anyMap()))
				.thenReturn(ABLE_TO_VOTE);
		doReturn(false).when(this.votoRepository).existsVotoByIdSessaoAndIdAssociado(Mockito.anyLong(),
				Mockito.anyLong());
		doReturn(SerializationUtils.clone(voto)).when(this.votoRepository).save(Mockito.any(Voto.class));
		Voto votoNovo = this.votoService.votar(voto.getSessao().getId(), voto.getAssociado().getCPF(), voto.getValor());
		assertNotNull(votoNovo);
		assertTrue(new ReflectionEquals(votoNovo).matches(voto));
	}

	@Test
	@DisplayName("Verifica votar com sessao fechada")
	void votarSessaoFechada() throws NotFoundException, DateTimeException, ConflictException, JsonMappingException,
			JsonProcessingException, BadRequestException {
		Voto voto = criaVoto();
		OffsetDateTime currentDateTime = OffsetDateTime.now();
		voto.getSessao().setDataHoraInicio(currentDateTime.minusHours(2));
		voto.getSessao().setDataHoraFechamento(currentDateTime.minusHours(1));
		doReturn(currentDateTime).when(this.dateTimeUtil).currentDateTime();
		doReturn(voto.getSessao()).when(this.sessaoService).obterPorId(Mockito.anyLong());
		doReturn(Optional.of(voto.getAssociado())).when(this.associadoService).obterPorCPF(Mockito.anyString());
		when(this.restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class), Mockito.anyMap()))
				.thenReturn(ABLE_TO_VOTE);
		assertThrows(DateTimeException.class,
				() -> this.votoService.votar(voto.getSessao().getId(), voto.getAssociado().getCPF(), voto.getValor()),
				"Sessão não está aberta");
	}

	@Test
	@DisplayName("Verifica votar com associado inexistente")
	void votarAssociadoInexistente() throws NotFoundException, DateTimeException, ConflictException,
			JsonMappingException, JsonProcessingException, BadRequestException {
		Voto voto = criaVoto();
		doReturn(voto.getSessao().getDataHoraInicio()).when(this.dateTimeUtil).currentDateTime();
		doReturn(voto.getSessao()).when(this.sessaoService).obterPorId(Mockito.anyLong());
		doReturn(Optional.empty()).when(this.associadoService).obterPorCPF(Mockito.anyString());
		assertThrows(NotFoundException.class,
				() -> this.votoService.votar(voto.getSessao().getId(), voto.getAssociado().getCPF(), voto.getValor()),
				"Associado inexistente");
	}

	@Test
	@DisplayName("Verifica votar com associado nao votante")
	void votarAssociadoNaoVotante() throws NotFoundException, DateTimeException, ConflictException,
			JsonMappingException, JsonProcessingException, BadRequestException {
		Voto voto = criaVoto();
		doReturn(voto.getSessao().getDataHoraInicio()).when(this.dateTimeUtil).currentDateTime();
		doReturn(voto.getSessao()).when(this.sessaoService).obterPorId(Mockito.anyLong());
		doReturn(Optional.of(voto.getAssociado())).when(this.associadoService).obterPorCPF(Mockito.anyString());
		when(this.restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class), Mockito.anyMap()))
				.thenReturn(UNABLE_TO_VOTE);
		assertThrows(BadRequestException.class,
				() -> this.votoService.votar(voto.getSessao().getId(), voto.getAssociado().getCPF(), voto.getValor()),
				"Associado não autorizado a votar");
	}

	@Test
	@DisplayName("Verifica votar com associado que ja votou")
	void votarAssociadoJaVotou() throws NotFoundException, DateTimeException, ConflictException, JsonMappingException,
			JsonProcessingException, BadRequestException {
		Voto voto = criaVoto();
		doReturn(voto.getSessao().getDataHoraInicio()).when(this.dateTimeUtil).currentDateTime();
		doReturn(voto.getSessao()).when(this.sessaoService).obterPorId(Mockito.anyLong());
		doReturn(Optional.of(voto.getAssociado())).when(this.associadoService).obterPorCPF(Mockito.anyString());
		when(this.restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class), Mockito.anyMap()))
				.thenReturn(ABLE_TO_VOTE);
		doReturn(true).when(this.votoRepository).existsVotoByIdSessaoAndIdAssociado(Mockito.anyLong(),
				Mockito.anyLong());
		assertThrows(ConflictException.class,
				() -> this.votoService.votar(voto.getSessao().getId(), voto.getAssociado().getCPF(), voto.getValor()),
				"O associado já votou");
	}

	@Test
	@DisplayName("Verifica voto existente por id sessão e id associado")
	void verificaExistePorIdSessaoIdAssociado() throws NotFoundException, DateTimeException, ConflictException {
		doReturn(true).when(this.votoRepository).existsVotoByIdSessaoAndIdAssociado(Mockito.anyLong(),
				Mockito.anyLong());
		assertTrue(this.votoService.verificaExistePorIdSessaoIdAssociado(Long.valueOf(1), Long.valueOf(1)));
	}

	@Test
	@DisplayName("Verifica voto nao existente por id sessão e id associado")
	void verificaNaoExistePorIdSessaoIdAssociado() throws NotFoundException, DateTimeException, ConflictException {
		doReturn(false).when(this.votoRepository).existsVotoByIdSessaoAndIdAssociado(Mockito.anyLong(),
				Mockito.anyLong());
		assertFalse(this.votoService.verificaExistePorIdSessaoIdAssociado(Long.valueOf(1), Long.valueOf(1)));
	}
}