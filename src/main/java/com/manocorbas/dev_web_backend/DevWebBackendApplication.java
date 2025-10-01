package com.manocorbas.dev_web_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevWebBackendApplication {

	public static void main(String[] args) {
		// A lib buscará um arquivo .env na pasta raiz do projeto e
		// definirá as variaveis como propriedades do sistema.
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		SpringApplication.run(DevWebBackendApplication.class, args);
	}

}
