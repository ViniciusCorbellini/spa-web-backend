package com.manocorbas.dev_web_backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagemService {

    @Value("${application.upload.dir}") 
    private String uploadDir;

    Path dirUpload = Paths.get(uploadDir);

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

    public void deletarImagem(String fotoPerfil) {

        if (fotoPerfil == null || fotoPerfil.isBlank()) {
            return;
        }

        try {
            // fotoPerfil vem no formato: "/uploads/perfis/<foto>.jpg"
            // Pegamos só o nome:/
            String nomeArquivo = Paths.get(fotoPerfil).getFileName().toString();

            // Caminho real do arquivo
            Path caminhoArquivo = dirUpload.resolve(nomeArquivo);

            // Se existir, deleta
            if (Files.exists(caminhoArquivo)) {
                Files.delete(caminhoArquivo);
            }

        } catch (Exception e) {
            System.err.println("Erro ao deletar imagem: " + e.getMessage());
        }
    }

}