package com.manocorbas.dev_web_backend.services;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class ImagemService {

    private final AmazonS3 s3Client;

    @Value("${supabase.storage.bucket-name}")
    private String bucketName;

    // URL base do projeto para montar o link público
    @Value("${supabase.project.url}")
    private String supabaseProjectUrl;

    public ImagemService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

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

        String extensao = contentType.equals("image/png") ? ".png" : ".jpg";
        String nomeArquivo = UUID.randomUUID() + extensao;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(foto.getContentType());
        metadata.setContentLength(foto.getSize());

        // Envia para o Supabase
        s3Client.putObject(bucketName, nomeArquivo, foto.getInputStream(), metadata);

        // Retorna a URL Pública montada manualmente
        // Formato:
        // https://<projeto>.supabase.co/storage/v1/object/public/<bucket>/<nomeArquivo>
        return supabaseProjectUrl + "/storage/v1/object/public/" + bucketName + "/" + nomeArquivo;
    }

    public void deletarImagem(String fotoPerfil) {

        if (fotoPerfil == null || fotoPerfil.isBlank()) {
            return;
        }

        try {
            // Extrai o nome do arquivo da URL completa
            // formato: https://xxx.supabase.co/.../public/imagens-perfil/nome-do-arquivo.jpg
            // fazemos uma substring a partir do idx dá ultima '/'
            String nomeArquivo = fotoPerfil.substring(fotoPerfil.lastIndexOf("/") + 1);

            // deleta usando o cliente s3
            s3Client.deleteObject(bucketName, nomeArquivo);

            System.out.println("Imagem deletada do Supabase: " + nomeArquivo);

        } catch (Exception e) {
            System.err.println("Erro ao deletar imagem do storage: " + e.getMessage());
        }
    }
}
