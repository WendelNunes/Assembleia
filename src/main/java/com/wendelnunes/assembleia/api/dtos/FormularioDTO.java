package com.wendelnunes.assembleia.api.dtos;

import static java.util.Arrays.asList;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wendelnunes.assembleia.domain.entities.Associado;
import com.wendelnunes.assembleia.domain.entities.Pauta;
import com.wendelnunes.assembleia.domain.entities.ResultadoSessao;
import com.wendelnunes.assembleia.domain.entities.Sessao;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(value = Include.NON_NULL)
@ApiModel(value = "Formulario")
public class FormularioDTO {

	private final String tipo = "FORMULARIO";
	private String titulo;
	private List<FormularioItemDTO> itens;
	private BotaoDTO botaoOk;
	private BotaoDTO botaoCancelar;

	public static FormularioDTO from(ResultadoSessao resultadoSessao, String url) {
		return FormularioDTO.builder() //
				.titulo("Resultado da Sessão") //
				.itens(asList( //
						FormularioItemDTO.builder() //
								.id("estado") //
								.tipo(TipoFormularioItemDTO.TEXTO) //
								.valor(resultadoSessao.getAberta() ? "Aberta" : "Fechada") //
								.build(), //
						FormularioItemDTO.builder() //
								.id("descricaoSessao") //
								.tipo(TipoFormularioItemDTO.TEXTO) //
								.valor(resultadoSessao.getSessao().getDescricao()) //
								.build(), //
						FormularioItemDTO.builder() //
								.id("totalSim") //
								.tipo(TipoFormularioItemDTO.NUMERO) //
								.valor(resultadoSessao.getTotalSim()) //
								.build(),
						FormularioItemDTO.builder() //
								.id("totalNao") //
								.tipo(TipoFormularioItemDTO.NUMERO) //
								.valor(resultadoSessao.getTotalNao()) //
								.build()))
				.botaoCancelar(BotaoDTO.builder() //
						.texto("Cancelar") //
						.url(url) //
						.build()) //
				.build(); //
	}

	public static FormularioDTO from(Sessao sessao, String url) {
		return FormularioDTO.builder() //
				.titulo("Cadastro de Sessão") //
				.itens(asList( //
						FormularioItemDTO.builder() //
								.id("idPauta") //
								.tipo(TipoFormularioItemDTO.NUMERO) //
								.valor(sessao.getPauta().getId()) //
								.build(), //
						FormularioItemDTO.builder() //
								.id("descricao") //
								.tipo(TipoFormularioItemDTO.INPUT_TEXTO) //
								.valor(sessao.getDescricao()) //
								.build(),
						FormularioItemDTO.builder() //
								.id("dataHoraInicio") //
								.tipo(TipoFormularioItemDTO.INPUT_DATA_HORA) //
								.valor(sessao.getDataHoraInicio()) //
								.build(),
						FormularioItemDTO.builder() //
								.id("dataHoraFechamento") //
								.tipo(TipoFormularioItemDTO.INPUT_DATA_HORA) //
								.valor(sessao.getDataHoraFechamento()) //
								.build()))
				.botaoOk(BotaoDTO.builder() //
						.texto("Salvar") //
						.url(url + "/" + sessao.getId()) //
						.build()) //
				.botaoCancelar(BotaoDTO.builder() //
						.texto("Cancelar") //
						.url(url) //
						.build()) //
				.build(); //
	}

	public static FormularioDTO from(Associado associado, String url) {
		return FormularioDTO.builder() //
				.titulo("Cadastro de Associado") //
				.itens(asList( //
						FormularioItemDTO.builder() //
								.id("CPF") //
								.tipo(TipoFormularioItemDTO.INPUT_TEXTO) //
								.valor(associado.getCPF()) //
								.build(), //
						FormularioItemDTO.builder() //
								.id("nome") //
								.tipo(TipoFormularioItemDTO.INPUT_TEXTO) //
								.valor(associado.getNome()) //
								.build()))
				.botaoOk(BotaoDTO.builder() //
						.texto("Salvar") //
						.url(url + "/" + associado.getId()) //
						.build()) //
				.botaoCancelar(BotaoDTO.builder() //
						.texto("Cancelar") //
						.url(url) //
						.build()) //
				.build(); //
	}

	public static FormularioDTO from(Pauta pauta, String url) {
		return FormularioDTO.builder() //
				.titulo("Cadastro de Pauta") //
				.itens(asList( //
						FormularioItemDTO.builder() //
								.id("nome") //
								.tipo(TipoFormularioItemDTO.INPUT_TEXTO) //
								.valor(pauta.getNome()) //
								.build())) //
				.botaoOk(BotaoDTO.builder() //
						.texto("Salvar") //
						.url(url + "/" + pauta.getId()) //
						.build()) //
				.botaoCancelar(BotaoDTO.builder() //
						.texto("Cancelar") //
						.url(url) //
						.build()) //
				.build(); //
	}
}