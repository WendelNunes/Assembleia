package com.wendelnunes.assembleia.domain.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.repositories.PautaRepository;
import com.wendelnunes.assembleia.exceptions.NotDeleteException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PautaService {

	private PautaRepository repository;

	public Pauta criar(Pauta pauta) {
		return this.repository.save(pauta);
	}

	public Pauta atualizar(Pauta pauta) throws NotFoundException {
		if (!this.repository.existsById(pauta.getId())) {
			throw new NotFoundException("Pauta inexistente");
		}
		return this.repository.save(pauta);
	}

	public void deletar(Long id) throws NotFoundException, NotDeleteException {
		Pauta pauta = this.repository.findById(id).orElseThrow(() -> new NotFoundException("Pauta inexistente"));
		if (!pauta.getSessoes().isEmpty()) {
			throw new NotDeleteException("Não é possível deletar a pauta pois existe sessões para a mesma");
		}
		this.repository.deleteById(id);
	}

	public List<Pauta> obterTodos() {
		return this.repository.findAll();
	}

	public Pauta obterPorId(Long id) throws NotFoundException {
		return this.repository.findById(id).orElseThrow(() -> new NotFoundException("Pauta inexistente"));
	}

	public boolean verificaExistePautaPorId(Long id) {
		return this.repository.existsById(id);
	}
}