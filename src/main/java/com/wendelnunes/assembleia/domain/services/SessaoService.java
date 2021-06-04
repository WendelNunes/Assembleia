package com.wendelnunes.assembleia.domain.services;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.repositories.SessaoRepository;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.DateTimeUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SessaoService {

	private SessaoRepository sessaoRepository;
	private PautaService pautaService;
	private DateTimeUtil dateTimeUtil;

	public Sessao abrir(Sessao sessao) throws DateTimeException, NotFoundException {
		if (!this.pautaService.verificaExistePautaPorId(sessao.getPauta().getId())) {
			throw new NotFoundException("Pauta inexistente");
		}
		if (sessao.getDataHoraInicio().isBefore(this.dateTimeUtil.currentDateTime())) {
			throw new DateTimeException("Data/Hora início deve ser maior ou igual a atual");
		}
		sessao.setDataHoraFechamento(Optional.ofNullable(sessao.getDataHoraFechamento()) //
				.orElse(sessao.getDataHoraInicio().plusMinutes(1))); //
		if (sessao.getDataHoraFechamento().isBefore(sessao.getDataHoraInicio())
				|| Duration.between(sessao.getDataHoraInicio(), sessao.getDataHoraFechamento()).toMinutes() < 1) {
			throw new DateTimeException("Data/Hora inicio e fechamento deve ter uma diferença de no mínimo 1 minuto");
		}
		return this.sessaoRepository.save(sessao);
	}

	public boolean verificaExistePorId(Long id) {
		return this.sessaoRepository.existsById(id);
	}

	public Sessao obterPorId(Long id) throws NotFoundException {
		return this.sessaoRepository.findById(id).orElseThrow(() -> new NotFoundException("Sessão inexistente"));
	}

	public List<Sessao> obterTodos() {
		return this.sessaoRepository.findAll();
	}
}