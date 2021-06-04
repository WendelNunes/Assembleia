package com.wendelnunes.assembleia.domain.services;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.entities.Voto;
import com.wendelnunes.assembleia.domain.repositories.VotoRepository;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.DateTimeUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VotoService {

	private VotoRepository votoRepository;
	private SessaoService sessaoService;
	private AssociadoService associadoService;
	private DateTimeUtil dateTimeUtil;

	public Voto votar(Voto voto) throws NotFoundException, DateTimeException, ConflictException {
		Sessao sessao = this.sessaoService.obterPorId(voto.getSessao().getId());
		OffsetDateTime now = this.dateTimeUtil.currentDateTime();
		if (!((now.isBefore(sessao.getDataHoraFechamento()) || now.isEqual(sessao.getDataHoraFechamento()))
				&& (now.isAfter(sessao.getDataHoraInicio()) || now.isEqual(sessao.getDataHoraInicio())))) {
			throw new DateTimeException("Sessão não está aberta");
		}
		if (!this.associadoService.verificaExistePorId(voto.getAssociado().getId())) {
			throw new NotFoundException("Associado inexistente");
		}
		if (this.verificaExistePorIdSessaoIdAssociado(voto.getSessao().getId(), voto.getAssociado().getId())) {
			throw new ConflictException("O associado já votou");
		}
		return this.votoRepository.save(voto);
	}

	public boolean verificaExistePorIdSessaoIdAssociado(Long idSessao, Long idAssociado) {
		return this.votoRepository.existsVotoByIdSessaoAndIdAssociado(idSessao, idAssociado);
	}
}