package com.wendelnunes.assembleia.domain.services;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.repositories.AssociadoRepository;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

@SpringBootTest
public class AssociadoServiceTest {

	@Mock
	private AssociadoRepository associadoRepository;
	@InjectMocks
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
	void verificaAssociadoCriadoComSucesso() throws ConflictException {
		Associado associado = criaAssociado();
		when(this.associadoRepository.save(Mockito.any(Associado.class))).thenReturn(associado);
		when(this.associadoRepository.findByCPF(Mockito.anyString())).thenReturn(Optional.empty());
		Associado associadoNovo = SerializationUtils.clone(associado);
		associadoNovo.setId(null);
		associadoNovo = this.associadoService.criar(associadoNovo);
		assertNotNull(associadoNovo);
		assertTrue(new ReflectionEquals(associadoNovo).matches(associado));
	}

	@Test
	@DisplayName("Verifica criar associado com CPF já cadastrado")
	void verificaCriarAssociadoCPFJaCadastrado() throws ConflictException {
		Associado associado = criaAssociado();
		when(this.associadoRepository.findByCPF(Mockito.anyString())).thenReturn(Optional.of(associado));
		Associado associadoNovo = SerializationUtils.clone(associado);
		associadoNovo.setId(null);
		assertThrows(ConflictException.class, () -> this.associadoService.criar(associadoNovo), "CPF já cadastrado");
	}

	@Test
	@DisplayName("Verifica atualizar associado")
	void verificaAtualizarAssociado() throws ConflictException {
		Associado associado = criaAssociado();
		when(this.associadoRepository.save(Mockito.any(Associado.class))).thenReturn(associado);
		Associado associadoNovo = this.associadoService.criar(SerializationUtils.clone(associado));
		assertNotNull(associadoNovo);
		assertTrue(new ReflectionEquals(associadoNovo).matches(associado));
	}

	@Test
	@DisplayName("Verifica atualizar associado com CPF já cadastrado para outro usuario")
	void verificaAtualizarAssociadoCPFCadastradoOutroUsuario() throws ConflictException {
		Associado associado = criaAssociado();
		when(this.associadoService.verificaExistePorId(Mockito.anyLong())).thenReturn(true);
		when(this.associadoRepository.findByCPF(Mockito.anyString())).thenReturn(Optional.of(associado));
		Associado associadoNovo = criaAssociadoDois();
		associadoNovo.setCPF(associado.getCPF());
		assertThrows(ConflictException.class, () -> this.associadoService.atualizar(associadoNovo),
				"CPF já cadastrado");
	}

	@Test
	@DisplayName("Verifica atualizar CPF do associado")
	void verificaAtualizarCPFDoAssociado() throws ConflictException, NotFoundException {
		Associado associado = criaAssociado();
		String cpf = "15016460019";
		Associado associadoAtualizado = SerializationUtils.clone(associado);
		associadoAtualizado.setCPF(cpf);
		when(this.associadoService.verificaExistePorId(Mockito.anyLong())).thenReturn(true);
		when(this.associadoRepository.findByCPF(Mockito.anyString())).thenReturn(Optional.of(associado));
		when(this.associadoRepository.save(Mockito.any(Associado.class))).thenReturn(associadoAtualizado);
		associadoAtualizado = this.associadoService.atualizar(associadoAtualizado);
		assertNotNull(associadoAtualizado);
		assertTrue(new ReflectionEquals(associadoAtualizado, "CPF").matches(associado));
		assertEquals(associadoAtualizado.getCPF(), cpf);
	}

	@Test
	@DisplayName("Verifica deletar associado")
	void deletarAssociado() throws NotFoundException {
		Long id = Long.valueOf(1);
		when(this.associadoService.verificaExistePorId(Mockito.anyLong())).thenReturn(true);
		this.associadoService.deletar(id);
		verify(this.associadoRepository).deleteById(id);
	}

	@Test
	@DisplayName("Verifica deletar associado inexistente")
	void deletarAssociadoInexistente() throws NotFoundException {
		when(this.associadoService.verificaExistePorId(Mockito.anyLong())).thenReturn(false);
		assertThrows(NotFoundException.class, () -> this.associadoService.deletar(Long.valueOf(1)),
				"Associado inexistente");
	}

	@Test
	@DisplayName("Verifica obter associado por id")
	void obterAssociadoPorId() throws NotFoundException {
		Associado associado = criaAssociado();
		when(this.associadoRepository.findById(Mockito.anyLong()))
				.thenReturn(Optional.of(SerializationUtils.clone(associado)));
		Associado associadoRetornado = this.associadoService.obterPorId(Long.valueOf(1));
		assertNotNull(associadoRetornado);
		assertTrue(new ReflectionEquals(associadoRetornado).matches(associado));
	}

	@Test
	@DisplayName("Verifica obter associado por id inexistente")
	void obterAssociadoPorIdInexistente() throws NotFoundException {
		when(this.associadoRepository.findById(Mockito.anyLong())).thenReturn(null);
		Associado associado = this.associadoService.obterPorId(Long.valueOf(1));
		assertNull(associado);
	}

	@Test
	@DisplayName("Verifica obter todos")
	void obterTodos() throws NotFoundException {
		List<Associado> associados = asList(criaAssociado(), criaAssociadoDois());
		doReturn(associados).when(this.associadoRepository).findAll();
		List<Associado> associadosRetornados = this.associadoService.obterTodos();
		assertEquals(associados.size(), associadosRetornados.size());
	}

	@Test
	@DisplayName("Verifica obter associado por CPF")
	void obterAssociadoPorCPF() throws NotFoundException {
		Associado associado = criaAssociado();
		when(this.associadoRepository.findByCPF(Mockito.anyString()))
				.thenReturn(Optional.of(SerializationUtils.clone(associado)));
		Optional<Associado> optionalAssociado = this.associadoService.obterPorCPF("03492767141");
		assertNotNull(optionalAssociado);
		assertTrue(optionalAssociado.isPresent());
		Associado associadoRetornado = optionalAssociado.get();
		assertTrue(new ReflectionEquals(associadoRetornado).matches(associado));
	}

	@Test
	@DisplayName("Verifica obter associado por CPF inexistente")
	void obterAssociadoPorCPFInexistente() throws NotFoundException {
		when(this.associadoRepository.findByCPF(Mockito.anyString())).thenReturn(Optional.empty());
		Optional<Associado> optionalAssociado = this.associadoService.obterPorCPF("03492767141");
		assertTrue(!optionalAssociado.isPresent());
	}

	@Test
	@DisplayName("Verifica se associado existe por id")
	void verificaExistePorId() throws NotFoundException {
		when(this.associadoRepository.existsById(Mockito.anyLong())).thenReturn(true);
		assertTrue(this.associadoService.verificaExistePorId(Long.valueOf(1)));
	}

}