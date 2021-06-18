package com.wendelnunes.assembleia.domain.services;

import static com.wendelnunes.assembleia.utils.StringUtil.removeMask;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wendelnunes.assembleia.clients.UserInfoClient;
import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.entities.Voto;
import com.wendelnunes.assembleia.domain.repositories.VotoRepository;
import com.wendelnunes.assembleia.exceptions.BadRequestException;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VotoService {

	private VotoRepository votoRepository;
	private SessaoService sessaoService;
	private AssociadoService associadoService;
	private UserInfoClient userInfoClient;

	public Voto votar(Long idSessao, String CPF, Boolean valor) throws NotFoundException, DateTimeException,
			ConflictException, JsonMappingException, JsonProcessingException, BadRequestException {
		Sessao sessao = this.sessaoService.obterPorId(idSessao);
		if (!this.verificaSessaoAberta(sessao)) {
			throw new DateTimeException("Sessão não está aberta");
		}
		CPF = removeMask(CPF);
		Associado associado = this.associadoService.obterPorCPF(CPF)
				.orElseThrow(() -> new NotFoundException("Associado inexistente"));
		if (!this.userInfoClient.verificaAssociadoVotante(CPF)) {
			throw new BadRequestException("Associado não autorizado a votar");
		}
		if (this.verificaExistePorIdSessaoIdAssociado(sessao.getId(), associado.getId())) {
			throw new ConflictException("O associado já votou");
		}
		Voto voto = new Voto();
		voto.setSessao(sessao);
		voto.setAssociado(associado);
		voto.setValor(valor);
		return this.votoRepository.save(voto);
	}

	public boolean verificaSessaoAberta(Sessao sessao) {
		OffsetDateTime now = OffsetDateTime.now();
		return now.compareTo(sessao.getDataHoraInicio()) >= 0 && now.compareTo(sessao.getDataHoraFechamento()) <= 0;
	}

	public boolean verificaExistePorIdSessaoIdAssociado(Long idSessao, Long idAssociado) {
		return this.votoRepository.existsVotoByIdSessaoAndIdAssociado(idSessao, idAssociado);
	}
}