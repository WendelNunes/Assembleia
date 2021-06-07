package com.wendelnunes.assembleia.api.dtos;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.entities.Sessao;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Selecao")
public class SelecaoDTO {

	private final String tipo = "SELECAO";
	private String titulo;
	private List<SelecaoItemDTO> itens;

	public static SelecaoDTO fromPauta(List<Pauta> pautas, String url) {
		return SelecaoDTO.builder() //
				.titulo("Lista de Pautas") //
				.itens(pautas.stream().map(a -> SelecaoItemDTO.builder() //
						.texto(a.getNome()) //
						.url(url + "/" + a.getId()) //
						.build()).collect(Collectors.toList())) //
				.build(); //
	}

	public static SelecaoDTO fromAssociado(List<Associado> associados, String url) {
		return SelecaoDTO.builder() //
				.titulo("Lista de Associados") //
				.itens(associados.stream().map(a -> SelecaoItemDTO.builder() //
						.texto(a.getNome()) //
						.url(url + "/" + a.getId()) //
						.build()).collect(Collectors.toList())) //
				.build(); //
	}

	public static SelecaoDTO fromSessao(List<Sessao> sessoes, String url) {
		return SelecaoDTO.builder() //
				.titulo("Lista de Sessões") //
				.itens(sessoes.stream().map(a -> SelecaoItemDTO.builder() //
						.texto(a.getDescricao()) //
						.url(url + "/" + a.getId()) //
						.build()).collect(Collectors.toList())) //
				.build(); //
	}
}