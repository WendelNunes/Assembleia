package com.wendelnunes.assembleia.domain.services;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.boot.test.context.SpringBootTest;

import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.repositories.SessaoRepository;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.DateTimeUtil;

@SpringBootTest
class SessaoServiceTest {

	@Mock
	private SessaoRepository sessaoRepository;
	@Mock
	private PautaService pautaService;
	@Mock
	private DateTimeUtil dateTimeUtil;
	@InjectMocks
	private SessaoService sessaoService;

	private static Sessao criaSessao(Long id, String descricao, OffsetDateTime dataHoraInicio,
			OffsetDateTime dataHoraFinal) {
		Sessao sessao = new Sessao();
		sessao.setId(id);
		sessao.setDescricao(descricao);
		sessao.setDataHoraInicio(dataHoraInicio);
		sessao.setDataHoraFechamento(dataHoraFinal);
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
	@DisplayName("Verifica abrir sessão")
	void abrirSessao() throws DateTimeException, NotFoundException {
		Sessao sessao = criaSessao();
		when(this.dateTimeUtil.currentDateTime()).thenReturn(sessao.getDataHoraInicio());
		when(this.sessaoRepository.save(Mockito.any(Sessao.class))).thenReturn(sessao);
		Sessao sessaoNova = SerializationUtils.clone(sessao);
		sessaoNova.setId(null);
		sessaoNova = this.sessaoService.abrir(sessaoNova);
		assertNotNull(sessaoNova);
		assertTrue(new ReflectionEquals(sessaoNova).matches(sessao));
	}

	@Test
	@DisplayName("Verifica abrir sessão com data/hora fechamento default")
	void abrirSessaoComDataHoraFechamentoDefault() throws DateTimeException, NotFoundException {
		Sessao sessao = criaSessao();
		sessao.setDataHoraFechamento(sessao.getDataHoraInicio().plusMinutes(1));
		when(this.dateTimeUtil.currentDateTime()).thenReturn(sessao.getDataHoraInicio());
		when(this.sessaoRepository.save(Mockito.any(Sessao.class))).thenReturn(sessao);
		Sessao sessaoNova = SerializationUtils.clone(sessao);
		sessaoNova.setId(null);
		sessaoNova.setDataHoraFechamento(null);
		sessaoNova = this.sessaoService.abrir(sessaoNova);
		assertNotNull(sessaoNova);
		assertTrue(new ReflectionEquals(sessaoNova, "dataHoraFechamento").matches(sessao));
		assertEquals(sessaoNova.getDataHoraFechamento(), sessao.getDataHoraFechamento());
	}

	@Test
	@DisplayName("Verifica abrir sessão com pauta inexistente")
	void abrirSessaoComPautaInexistente() throws DateTimeException {
		Sessao sessao = criaSessao();
		when(this.pautaService.verificaExistePautaPorId(Mockito.anyLong())).thenReturn(false);
		assertThrows(NotFoundException.class, () -> this.sessaoService.abrir(sessao), "Pauta inexistente");
	}

	@Test
	@DisplayName("Verifica abrir sessão com data/hora inicio anterior a atual")
	void abrirSessaoComDataHoraInicioAnteriorAtual() throws DateTimeException {
		Sessao sessao = criaSessao();
		when(this.dateTimeUtil.currentDateTime()).thenReturn(sessao.getDataHoraInicio());
		sessao.setDataHoraInicio(sessao.getDataHoraInicio().minusHours(1));
		assertThrows(DateTimeException.class, () -> this.sessaoService.abrir(sessao),
				"Data/Hora início deve ser maior ou igual a atual");
	}

	@Test
	@DisplayName("Verifica abrir sessão com data/hora inicio e fechamento com diferença de no mínimo 1 minuto")
	void abrirSessaoComDataHoraInicioFechamentoDiferencaMinimaUmMinuto() throws DateTimeException {
		Sessao sessao = criaSessao();
		when(this.dateTimeUtil.currentDateTime()).thenReturn(sessao.getDataHoraInicio());
		sessao.setDataHoraFechamento(this.dateTimeUtil.currentDateTime().plusSeconds(30));
		assertThrows(DateTimeException.class, () -> this.sessaoService.abrir(sessao),
				"Data/Hora inicio e fechamento deve ter uma diferença de no mínimo 1 minuto");
	}

	@Test
	@DisplayName("Verifica obter todas sessões")
	void obterTodos() throws NotFoundException {
		List<Sessao> sessaos = asList(criaSessao(), criaSessaoDois());
		doReturn(sessaos).when(this.sessaoRepository).findAll();
		List<Sessao> sessaosRetornadas = this.sessaoService.obterTodos();
		assertEquals(sessaos.size(), sessaosRetornadas.size());
	}

	@Test
	@DisplayName("Verifica obter sessao por id")
	void obterSessaoPorId() throws NotFoundException {
		Sessao sessao = criaSessao();
		doReturn(Optional.of(SerializationUtils.clone(sessao))).when(this.sessaoRepository).findById(Mockito.anyLong());
		Sessao sessaoRetornada = this.sessaoService.obterPorId(sessao.getId());
		assertNotNull(sessaoRetornada);
		assertTrue(new ReflectionEquals(sessaoRetornada).matches(sessao));
	}

	@Test
	@DisplayName("Verifica obter sessao por id inexistente")
	void obterSessaoPorIdInexistente() throws NotFoundException {
		when(this.sessaoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> this.sessaoService.obterPorId(Long.valueOf(1)),
				"Sessao inexistente");
	}
}