package com.manocorbas.dev_web_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class DevWebBackendApplicationTests {

	// ADICIONE ESTE MÉTODO E A ANOTAÇÃO
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		// Carrega o .env da raiz do projeto
		Dotenv dotenv = Dotenv.configure().load();

		// Define as propriedades para o contexto de teste do Spring
		registry.add("DB_URL", () -> dotenv.get("DB_URL"));
		registry.add("DB_USERNAME", () -> dotenv.get("DB_USERNAME"));
		registry.add("DB_PASSWORD", () -> dotenv.get("DB_PASSWORD"));
		registry.add("JWT_SECRET_KEY", () -> dotenv.get("JWT_SECRET_KEY"));
		registry.add("JWT_EXPIRATION_MS", () -> dotenv.get("JWT_EXPIRATION_MS"));
	}

	@Test
	void contextLoads() {
	}
}