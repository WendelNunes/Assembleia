package com.wendelnunes.assembleia.clients;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wendelnunes.assembleia.domain.entities.AssociadoVotante;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserInfoClient {

	public static final String URL = "https://user-info.herokuapp.com";
	private RestTemplate restTemplate;

	public boolean verificaAssociadoVotante(String CPF) throws JsonMappingException, JsonProcessingException {
		String json = this.restTemplate.getForObject(URL + "/users/{cpf}", String.class, Map.of("cpf", CPF));
		return AssociadoVotante.valueOf(new ObjectMapper().readTree(json).get("status").asText())
				.equals(AssociadoVotante.ABLE_TO_VOTE);

	}
}