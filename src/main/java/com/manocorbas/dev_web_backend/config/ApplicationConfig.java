package com.manocorbas.dev_web_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    private final UserDetailsService userDetailsService;

    // Injeção manual do UserDetailsService via construtor
    public ApplicationConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Define o provedor de autenticação.
     * O SecurityConfig injetará este bean.
     * Precisamos definir esses beans pra não quebrar a cadeia de autenticação
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        // Informa qual codificador de senha usar
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Define o gerenciador de autenticação.
     * Usado no controlador para autenticar usuários.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define o codificador de senha (BCrypt é o padrão e recomendado).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}