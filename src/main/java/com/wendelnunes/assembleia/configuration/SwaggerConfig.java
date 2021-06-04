package com.wendelnunes.assembleia.configuration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@AllArgsConstructor
public class SwaggerConfig {

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2) //
				.apiInfo(apiInfo()) //
				.useDefaultResponseMessages(false) //
				.tags( //
						new Tag("Associado", "API de manipulação de associados"), //
						new Tag("Pauta", "API de manipulação de pautas"), //
						new Tag("Sessão", "API de manipulação de sessões")) //
				.select() //
				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)) //
				.paths(PathSelectors.any()) //
				.build(); //
	}

	private ApiInfo apiInfo() {
		return new ApiInfo( //
				"API Assembleia Rest", //
				"Esta API Assembleia Rest descreve as operações para manipulação de votação de uma assembleia.", //
				"v1", //
				null, //
				new Contact("Wendel Nunes Donizete", null, "wendel.nunesdonizete@gmail.com"), //
				null, //
				null, //
				Collections.emptyList());
	}
}