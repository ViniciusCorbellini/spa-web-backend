package com.manocorbas.dev_web_backend.services;

import com.manocorbas.dev_web_backend.models.FraseAnonima;
import com.manocorbas.dev_web_backend.repositories.FraseAnonimaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraseAnonimaService {

    private final FraseAnonimaRepository fraseAnonimaRepository;

    public FraseAnonimaService(FraseAnonimaRepository fraseAnonimaRepository) {
        this.fraseAnonimaRepository = fraseAnonimaRepository;
    }

    @Transactional
    public FraseAnonima salvarFrase(String texto, LocalDateTime dataExpiracao) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Texto da frase não pode ser vazio");
        }
        if (texto.length() > 280) {
            throw new IllegalArgumentException("Texto ultrapassa o limite de 280 caracteres");
        }
        if (dataExpiracao == null || dataExpiracao.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data de expiração inválida");
        }

        FraseAnonima frase = new FraseAnonima();
        frase.setTexto(texto);
        frase.setDataExpiracao(dataExpiracao);
        // data_criacao vai pelo DEFAULT do banco

        return fraseAnonimaRepository.save(frase);
    }

    @Transactional(readOnly = true)
    public List<FraseAnonima> buscarPorPalavra(String palavra) {
        return fraseAnonimaRepository.findByTextoContainingIgnoreCase(palavra);
    }

    @Transactional(readOnly = true)
    public List<FraseAnonima> listarUltimasFrases() {
        return fraseAnonimaRepository.findTop10ByOrderByDataCriacaoDesc();
    }

    @Transactional
    public void removerExpiradas() {
        List<FraseAnonima> expiradas = fraseAnonimaRepository.findAll().stream()
                .filter(f -> f.getDataExpiracao().isBefore(LocalDateTime.now()))
                .toList();

        if (!expiradas.isEmpty()) {
            fraseAnonimaRepository.deleteAll(expiradas);
        }
    }
}