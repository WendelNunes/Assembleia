package com.wendelnunes.assembleia.configuration;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

	private AppConfig appConfig;

	@Override
	public void customize(ConfigurableWebServerFactory factory) {
		factory.setPort(Integer.valueOf(this.appConfig.getPort()));
	}
}