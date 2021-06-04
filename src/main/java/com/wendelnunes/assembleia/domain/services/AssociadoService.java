package com.wendelnunes.assembleia.domain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.repositories.AssociadoRepository;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AssociadoService {

	private AssociadoRepository repository;

	public Associado criar(Associado associado) throws ConflictException {
		Optional<Associado> associadoCadastrado = this.repository.findByCPF(associado.getCPF());
		if (associadoCadastrado.isPresent()) {
			throw new ConflictException("CPF já cadastrado");
		}
		return this.repository.save(associado);
	}

	public Associado atualizar(Associado associado) throws ConflictException, NotFoundException {
		if (!this.verificaExistePorId(associado.getId())) {
			throw new NotFoundException("Associado inexistente");
		}
		Optional<Associado> associadoCadastrado = this.repository.findByCPF(associado.getCPF());
		if (associadoCadastrado.isPresent() && !associado.equals(associadoCadastrado.get())) {
			throw new ConflictException("CPF já cadastrado");
		}
		return this.repository.save(associado);
	}

	public void deletar(Long id) throws NotFoundException {
		if (!this.verificaExistePorId(id)) {
			throw new NotFoundException("Associado inexistente");
		}
		this.repository.deleteById(id);
	}

	public Associado obterPorId(Long id) throws NotFoundException {
		return this.repository.findById(id).orElseThrow(() -> new NotFoundException("Associado inexistente"));
	}

	public List<Associado> obterTodos() {
		return this.repository.findAll();
	}

	public Optional<Associado> obterPorCPF(String CPF) {
		return this.repository.findByCPF(CPF);
	}

	public boolean verificaExistePorId(Long id) {
		return this.repository.existsById(id);
	}
}