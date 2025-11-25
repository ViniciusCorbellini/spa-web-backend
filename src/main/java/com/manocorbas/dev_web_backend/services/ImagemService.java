package com.manocorbas.dev_web_backend.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
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
        // Limpa a url pra extrair o nome real do arquivo
        filename = extractFilename(filename);

        Map<String, List<String>> body = Map.of("prefixes", List.of(filename));

        // Requisição DELETE com Body
        // Usa .method(HttpMethod.DELETE) em vez de .delete() para permitir o bodyValue
        client.method(HttpMethod.DELETE)
                .uri("/object/" + bucket)
                .header("Content-Type", "application/json") // exigência do supa
                .bodyValue(body) // Envia { "prefixes": ["string"] } 
                // ^^^ necessário conforme a doc em https://supabase.github.io/storage/#/object/delete_object__bucketName_
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public String getPublicUrl(String filename) {
        return projectUrl + "/storage/v1/object/public/" + bucket + "/" + filename;
    }

    public String extractFilename(String filename) {
        if (!filename.startsWith("http")) {
            return filename;
        }

        String pathToRemove = "/public/" + bucket + "/";
        int index = filename.indexOf(pathToRemove);

        if (index != -1) {
            return filename.substring(index + pathToRemove.length());
        }

        // Fallback caso a URL não tenha o formato esperado, tentamos limpar só o
        // /public/
        // mas isso evita enviar "bucket/arquivo.png"
        int publicIndex = filename.indexOf("/public/");

        if (publicIndex == -1) {
            return filename;
        }

        filename = filename.substring(publicIndex + "/public/".length());
        
        // Se o bucket ainda estiver no início, removemos
        if (filename.startsWith(bucket + "/")) {
            filename = filename.substring((bucket + "/").length());
        }

        return filename;
    }
}
