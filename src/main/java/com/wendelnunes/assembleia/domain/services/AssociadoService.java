package com.wendelnunes.assembleia.domain.services;

import static com.wendelnunes.assembleia.utils.SortUtil.createOrders;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.repositories.AssociadoRepository;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.StringUtil;

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
		associado.setCPF(StringUtil.removeMask(associado.getCPF()));
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
		associado.setCPF(StringUtil.removeMask(associado.getCPF()));
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

	public Page<Associado> obterTodos(Integer page, Integer pageSize, String... sort) {
		Pageable paging = PageRequest.of(Optional.ofNullable(page).orElse(0), Optional.ofNullable(pageSize).orElse(10),
				Sort.by(createOrders(sort)));
		return this.repository.findAll(paging);
	}

	public Optional<Associado> obterPorCPF(String CPF) {
		return this.repository.findByCPF(StringUtil.removeMask(CPF));
	}

	public boolean verificaExistePorId(Long id) {
		return this.repository.existsById(id);
	}
}