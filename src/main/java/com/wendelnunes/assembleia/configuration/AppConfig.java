package com.wendelnunes.assembleia.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("file:app.properties")
@Getter
public class AppConfig {

	// PORT
	@Value("${app.port}")
	private String port;
	// DATABASE
	@Value("${app.db.url}")
	private String databaseUrl;
	@Value("${app.db.name}")
	private String databaseName;
	@Value("${app.db.username}")
	private String databaseUsername;
	@Value("${app.db.password}")
	private String databasePassword;

	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create()//
				.url("jdbc:postgresql://" + this.databaseUrl + "/" + this.databaseName + "?ApplicationName=ERP")//
				.username(this.databaseUsername)//
				.password(this.databasePassword)//
				.driverClassName("org.postgresql.Driver")//
				.build();
	}
}