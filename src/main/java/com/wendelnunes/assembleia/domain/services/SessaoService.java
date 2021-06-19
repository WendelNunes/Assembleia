package com.wendelnunes.assembleia.domain.services;

import static com.wendelnunes.assembleia.utils.SortUtil.createOrders;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.repositories.SessaoRepository;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SessaoService {

	private SessaoRepository sessaoRepository;
	private PautaService pautaService;

	public Sessao abrir(Sessao sessao) throws DateTimeException, NotFoundException {
		if (!this.pautaService.verificaExistePautaPorId(sessao.getPauta().getId())) {
			throw new NotFoundException("Pauta inexistente");
		}
		if (!this.verificaSessaoComDataHoraMaiorIgualAtual(sessao)) {
			throw new DateTimeException("Data/Hora início deve ser maior ou igual a atual");
		}
		sessao.setDataHoraFechamento(Optional.ofNullable(sessao.getDataHoraFechamento()) //
				.orElse(sessao.getDataHoraInicio().plusMinutes(1))); //
		if (!this.verificaSessaoComDiferencaMinimaUmMinuto(sessao)) {
			throw new DateTimeException("Data/Hora inicio e fechamento deve ter uma diferença de no mínimo 1 minuto");
		}
		return this.sessaoRepository.save(sessao);
	}

	public boolean verificaSessaoComDataHoraMaiorIgualAtual(Sessao sessao) {
		return sessao.getDataHoraInicio().isBefore(OffsetDateTime.now());
	}

	public boolean verificaSessaoComDiferencaMinimaUmMinuto(Sessao sessao) {
		return sessao.getDataHoraFechamento().isBefore(sessao.getDataHoraInicio())
				|| Duration.between(sessao.getDataHoraInicio(), sessao.getDataHoraFechamento()).toMinutes() >= 1;
	}

	public boolean verificaExistePorId(Long id) {
		return this.sessaoRepository.existsById(id);
	}

	public Sessao obterPorId(Long id) throws NotFoundException {
		return this.sessaoRepository.findById(id).orElseThrow(() -> new NotFoundException("Sessão inexistente"));
	}

	public Page<Sessao> obterTodos(Integer page, Integer pageSize, String... sort) {
		Pageable paging = PageRequest.of(Optional.ofNullable(page).orElse(0), Optional.ofNullable(pageSize).orElse(10),
				Sort.by(createOrders(sort)));
		return this.sessaoRepository.findAll(paging);
	}
}