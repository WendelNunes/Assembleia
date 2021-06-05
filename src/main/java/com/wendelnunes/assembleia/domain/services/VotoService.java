package com.wendelnunes.assembleia.domain.services;

import static com.wendelnunes.assembleia.utils.StringUtil.removeMask;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.entities.AssociadoVotante;
import com.wendelnunes.assembleia.domain.entities.Sessao;
import com.wendelnunes.assembleia.domain.entities.Voto;
import com.wendelnunes.assembleia.domain.repositories.VotoRepository;
import com.wendelnunes.assembleia.exceptions.BadRequestException;
import com.wendelnunes.assembleia.exceptions.ConflictException;
import com.wendelnunes.assembleia.exceptions.DateTimeException;
import com.wendelnunes.assembleia.exceptions.NotFoundException;
import com.wendelnunes.assembleia.utils.DateTimeUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VotoService {

	public static final String URL_CHECK = "https://user-info.herokuapp.com/users/{cpf}";
	private VotoRepository votoRepository;
	private SessaoService sessaoService;
	private AssociadoService associadoService;
	private DateTimeUtil dateTimeUtil;
	private RestTemplate restTemplate;

	public Voto votar(Long idSessao, String CPF, Boolean valor) throws NotFoundException, DateTimeException,
			ConflictException, JsonMappingException, JsonProcessingException, BadRequestException {
		CPF = removeMask(CPF);
		Sessao sessao = this.sessaoService.obterPorId(idSessao);
		Associado associado = this.associadoService.obterPorCPF(CPF)
				.orElseThrow(() -> new NotFoundException("Associado inexistente"));
		if (!this.verificaAssociadoVotante(CPF)) {
			throw new BadRequestException("Associado não autorizado a votar");
		}
		OffsetDateTime now = this.dateTimeUtil.currentDateTime();
		if (!((now.isBefore(sessao.getDataHoraFechamento()) || now.isEqual(sessao.getDataHoraFechamento()))
				&& (now.isAfter(sessao.getDataHoraInicio()) || now.isEqual(sessao.getDataHoraInicio())))) {
			throw new DateTimeException("Sessão não está aberta");
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

	public boolean verificaExistePorIdSessaoIdAssociado(Long idSessao, Long idAssociado) {
		return this.votoRepository.existsVotoByIdSessaoAndIdAssociado(idSessao, idAssociado);
	}

	public boolean verificaAssociadoVotante(String CPF) throws JsonMappingException, JsonProcessingException {
		String json = this.restTemplate.getForObject(URL_CHECK, String.class, Map.of("cpf", CPF));
		return AssociadoVotante.valueOf(new ObjectMapper().readTree(json).get("status").asText())
				.equals(AssociadoVotante.ABLE_TO_VOTE);

	}
}