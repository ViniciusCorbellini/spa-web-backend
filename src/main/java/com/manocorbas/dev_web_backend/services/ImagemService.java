package com.manocorbas.dev_web_backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagemService {

    private final Path dirUpload = Paths.get("src/main/resources/uploads/perfis");

    public String salvarFotoPerfil(MultipartFile foto) throws IOException {

        if (foto.isEmpty()) {
            throw new IllegalArgumentException("Foto vazia");
        }

        // Validação da extensão da imagem
        String contentType = foto.getContentType();
        if (!Objects.equals(contentType, "image/jpeg") &&
            !Objects.equals(contentType, "image/png")) {
            throw new IllegalArgumentException("Apenas JPG ou PNG são permitidos");
        }

        // Cria o dir se ele não existir
        if (!Files.exists(dirUpload)) {
            Files.createDirectories(dirUpload);
        }

        String extensao = contentType.equals("image/png") ? ".png" : ".jpg";
        String nomeArquivo = UUID.randomUUID() + extensao;

        Path destino = dirUpload.resolve(nomeArquivo);

        Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        // Retorna o caminho onde a imagem foi salva
        return "/uploads/perfis/" + nomeArquivo;
    }
}