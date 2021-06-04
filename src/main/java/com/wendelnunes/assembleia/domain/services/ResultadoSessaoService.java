package com.wendelnunes.assembleia.domain.services;

import org.springframework.stereotype.Service;

import com.wendelnunes.assembleia.domain.entities.ResultadoSessao;
import com.wendelnunes.assembleia.domain.repositories.ResultadoSessaoRepository;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ResultadoSessaoService {

	private ResultadoSessaoRepository resultadoSessaoRepository;
	private SessaoService sessaoService;

	public ResultadoSessao obterPorIdSessao(Long idSessao) throws NotFoundException {
		if (!this.sessaoService.verificaExistePorId(idSessao)) {
			throw new NotFoundException("Sessão inexistente");
		}
		return this.resultadoSessaoRepository.getById(idSessao);
	}
}