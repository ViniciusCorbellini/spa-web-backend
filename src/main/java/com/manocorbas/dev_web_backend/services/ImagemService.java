package com.manocorbas.dev_web_backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

@Service
public class ImagemService {

    private final WebClient client;

    @Value("${supabase.bucket-name}")
    private String bucket;

    @Value("${supabase.project-url}")
    private String projectUrl;

    public ImagemService(WebClient supabaseClient) {
        this.client = supabaseClient;
    }

    public String salvarFotoPerfil(MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            client.put()
                    .uri("/object/" + bucket + "/" + filename)
                    .header("x-upsert", "true")
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return getPublicUrl(filename);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload para Supabase", e);
        }
    }

    public void deletarImagem(String filename) {
        // Caso o filename seja uma URL completa, extrai somente o path após /public/
        if (filename.startsWith("http")) {
            int index = filename.indexOf("/public/");
            if (index != -1) {
                filename = filename.substring(index + "/public/".length());
            }
        }

        // Supabase exige array JSON mesmo para deletar um único arquivo
        List<String> files = List.of(filename);

        ( // casting
            (RequestBodySpec) client.delete().uri("/object/" + bucket) // igual ao upload, baseUrl já está no client
        ) 
                .bodyValue(files)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public String getPublicUrl(String filename) {
        return projectUrl + "/storage/v1/object/public/" + bucket + "/" + filename;
    }
}
