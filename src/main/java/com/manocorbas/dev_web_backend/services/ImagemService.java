package com.manocorbas.dev_web_backend.services;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

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

    public String salvarFotoPerfil(MultipartFile arquivo) {
        String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();

        // Cria um arquivo temporário no disco do servidor (Render/Local)
        File arquivoTemp = null;

        // Gambiarra pra ver se o upload pro supabase funfa
        try {
            // Converte MultipartFile para File
            arquivoTemp = File.createTempFile("upload-", ".tmp");
            arquivo.transferTo(arquivoTemp);

            // Prepara a requisição
            PutObjectRequest request = new PutObjectRequest(bucketName, nomeArquivo, arquivoTemp);

            // Define permissão pública 
            request.setCannedAcl(CannedAccessControlList.PublicRead);

            // Envia (Agora o SDK pode tentar retry à vontade se a rede oscilar)
            s3Client.putObject(request);

            // Retorna a URL
            return supabaseProjectUrl + "/storage/v1/object/public/" + bucketName + "/" + nomeArquivo;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar arquivo temporário", e);
        } finally {
            // Limpeza: Deleta o arquivo temporário do servidor para não encher o disco
            if (arquivoTemp != null && arquivoTemp.exists()) {
                arquivoTemp.delete();
            }
        }
    }

    public void deletarImagem(String fotoPerfil) {

        if (fotoPerfil == null || fotoPerfil.isBlank()) {
            return;
        }

        try {
            // Extrai o nome do arquivo da URL completa
            // formato:
            // https://xxx.supabase.co/.../public/imagens-perfil/nome-do-arquivo.jpg
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
