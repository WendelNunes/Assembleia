package com.wendelnunes.assembleia.domain.services;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.repositories.PautaRepository;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.NotDeleteException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

@SpringBootTest
public class PautaServiceTest {

	@Mock
	private PautaRepository pautaRepository;
	@InjectMocks
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

	private static Pauta criaPautaComSessao() {
		Pauta pauta = criaPauta();
		pauta.setSessoes(new ArrayList<>());
		Sessao sessao = new Sessao();
		sessao.setId(Long.valueOf(1));
		sessao.setDescricao("Sessão 1");
		sessao.setPauta(pauta);
		OffsetDateTime now = OffsetDateTime.now();
		sessao.setDataHoraInicio(now);
		sessao.setDataHoraFechamento(now.plusHours(1));
		pauta.getSessoes().add(sessao);
		return pauta;
	}

	@Test
	@DisplayName("Verifica criar pauta")
	void verificaPautaCriadaComSucesso() {
		Pauta pauta = criaPauta();
		when(this.pautaRepository.save(Mockito.any(Pauta.class))).thenReturn(pauta);
		Pauta pautaNova = SerializationUtils.clone(pauta);
		pautaNova.setId(null);
		pautaNova = this.pautaService.criar(pautaNova);
		assertNotNull(pautaNova);
		assertTrue(new ReflectionEquals(pautaNova).matches(pauta));
	}

	@Test
	@DisplayName("Verifica atualizar pauta")
	void verificaAtualizarPauta() throws ConflictException {
		Pauta pauta = criaPauta();
		when(this.pautaRepository.save(Mockito.any(Pauta.class))).thenReturn(pauta);
		Pauta pautaNovo = this.pautaService.criar(SerializationUtils.clone(pauta));
		assertNotNull(pautaNovo);
		assertTrue(new ReflectionEquals(pautaNovo).matches(pauta));
	}

	@Test
	@DisplayName("Verifica atualizar pauta inexistente")
	void verificaAtualizarPautaInexistente() {
		when(this.pautaRepository.existsById(Mockito.anyLong())).thenReturn(false);
		assertThrows(NotFoundException.class, () -> this.pautaService.atualizar(criaPauta()), "Pauta inexistente");
	}

	@Test
	@DisplayName("Verifica deletar pauta")
	void deletarPauta() throws NotFoundException, NotDeleteException {
		Long id = Long.valueOf(1);
		Pauta pauta = criaPauta();
		when(this.pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(pauta));
		this.pautaService.deletar(id);
		verify(this.pautaRepository).findById(id);
		verify(this.pautaRepository).deleteById(id);
	}

	@Test
	@DisplayName("Verifica deletar pauta inexistente")
	void deletarPautaInexistente() throws NotFoundException, NotDeleteException {
		when(this.pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> this.pautaService.deletar(Long.valueOf(1)), "Pauta inexistente");
	}

	@Test
	@DisplayName("Verifica deletar pauta com sessão")
	void deletarPautaComSessao() throws NotFoundException, NotDeleteException {
		Pauta pautaComSessao = criaPautaComSessao();
		when(this.pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(pautaComSessao));
		assertThrows(NotDeleteException.class, () -> this.pautaService.deletar(pautaComSessao.getId()),
				"Não é possível deletar a pauta pois existe sessões para a mesma");
	}

	@Test
	@DisplayName("Verifica obter todas pautas")
	void obterTodos() throws NotFoundException {
		List<Pauta> pautas = asList(criaPauta(), criaPautaDois());
		when(this.pautaRepository.findAll(Mockito.any(Pageable.class))).thenReturn(new PageImpl<Pauta>(pautas));
		List<Pauta> pautasRetornadas = this.pautaService.obterTodos(0, 10, "id").getContent();
		assertEquals(pautas.size(), pautasRetornadas.size());
	}

	@Test
	@DisplayName("Verifica obter pauta por id")
	void obterPautaPorId() throws NotFoundException {
		Pauta pauta = criaPauta();
		when(this.pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(SerializationUtils.clone(pauta)));
		Pauta pautaRetornada = this.pautaService.obterPorId(pauta.getId());
		assertNotNull(pautaRetornada);
		assertTrue(new ReflectionEquals(pautaRetornada).matches(pauta));
	}

	@Test
	@DisplayName("Verifica obter pauta por id inexistente")
	void obterPautaPorIdInexistente() throws NotFoundException {
		when(this.pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> this.pautaService.obterPorId(Long.valueOf(1)), "Pauta inexistente");
	}
}