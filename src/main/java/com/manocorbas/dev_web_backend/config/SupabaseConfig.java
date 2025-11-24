package com.manocorbas.dev_web_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SupabaseConfig {
    
    @Value("${supabase.project-url}")
    private String projectUrl;

    @Value("${supabase.service-role}")
    private String serviceRole;

    @Bean
    public WebClient supabaseClient() {
        return WebClient.builder()
                .baseUrl(projectUrl + "/storage/v1")
                .defaultHeader("Authorization", "Bearer " + serviceRole)
                .build();
    }
}
