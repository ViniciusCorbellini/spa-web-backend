package com.manocorbas.dev_web_backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FunctionInitializer {
    private final JdbcTemplate jdbcTemplate;

    public FunctionInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init(){
        String initQuery = """ 
                    CREATE OR REPLACE FUNCTION remover_frases_expiradas()
                    RETURNS void AS $$
                    BEGIN
                        DELETE FROM frase_anonima
                        WHERE data_expiracao < NOW();
                    END;
                    $$ LANGUAGE plpgsql;
                """;

        jdbcTemplate.execute(initQuery);
    }
}
