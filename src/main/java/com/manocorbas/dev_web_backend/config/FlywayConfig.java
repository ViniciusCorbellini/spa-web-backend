package com.manocorbas.dev_web_backend.config;

import org.flywaydb.core.*;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    /**
     * Cria manualmente o bean do Flyway, garantindo que ele dependa do DataSource.
     * Isto substitui a auto-configuração do Spring Boot.
     */
    @Bean
    @DependsOn("dataSource") // Garante que o DataSource exista antes de configurar o Flyway
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
    }

    @Bean
    public FlywayMigrationStrategy clean(){
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }

    /**
     * Cria o inicializador que executa a migração, dependendo do bean Flyway que criámos acima.
     */
    @Bean
    @DependsOn("flyway")
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway, (f) -> {});
    }
}
