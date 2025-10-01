package com.manocorbas.dev_web_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@DependsOn("flywayInitializer") //
public class ApplicationConfig {

    private final UserDetailsService userDetailsService;

    // Injeção manual do UserDetailsService via construtor
    public ApplicationConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Define o provedor de autenticação.
     * O SecurityConfig injetará este bean.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Informa onde buscar os detalhes do usuário
        authProvider.setUserDetailsService(userDetailsService);

        // Informa qual codificador de senha usar
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Define o gerenciador de autenticação.
     * Usado no seu controlador para autenticar usuários.
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