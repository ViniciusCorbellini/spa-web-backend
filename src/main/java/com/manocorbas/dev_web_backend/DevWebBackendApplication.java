package com.manocorbas.dev_web_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DevWebBackendApplication {

	public static void main(String[] args) {
		// Configura para não quebrar se o .env não existir
		// Não quebra em prod (eu acho)
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		SpringApplication.run(DevWebBackendApplication.class, args);
	}

}
