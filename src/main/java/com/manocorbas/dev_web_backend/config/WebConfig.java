package com.manocorbas.dev_web_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Normaliza o caminho para garantir que termine com "/"
        String path = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        
        // Adiciona o prefixo "file:" necessário para o Spring achar arquivos no disco
        String location = "file:" + path;

        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations(location);
            
        // Log para debug 
        System.out.println("SERVINDO ARQUIVOS DE >>>> " + location);
    }
}
