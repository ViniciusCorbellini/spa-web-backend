package com.manocorbas.dev_web_backend.services;

import com.manocorbas.dev_web_backend.dtos.frase_anonima.FraseAnonimaDto;
import com.manocorbas.dev_web_backend.models.FraseAnonima;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.repositories.FraseAnonimaRepository;

import org.springframework.scheduling.annotation.Scheduled;
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
    public FraseAnonimaDto salvarFrase(String texto, Usuario usuario, LocalDateTime dataExpiracao) {
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
        frase.setUsuario(usuario);
        frase.setTexto(texto);
        frase.setDataExpiracao(dataExpiracao);
        // data_criacao vai por DEFAULT do banco (horário da inserção)

        FraseAnonima fraseAnonima = fraseAnonimaRepository.save(frase);
        return new FraseAnonimaDto(fraseAnonima.getId(), texto, fraseAnonima.getDataCriacao(), dataExpiracao);
    }

    @Transactional(readOnly = true)
    public List<FraseAnonima> buscarPorPalavra(String palavra) {
        List<FraseAnonima> lista = fraseAnonimaRepository.findByTextoContainingIgnoreCase(palavra);
        lista.stream().map(
            f -> new FraseAnonimaDto(f.getId(), f.getTexto(), f.getDataCriacao(), f.getDataExpiracao())
        );
        return fraseAnonimaRepository.findByTextoContainingIgnoreCase(palavra);
    }

    @Transactional(readOnly = true)
    public List<FraseAnonima> listarUltimasFrases() {
        return fraseAnonimaRepository.findTop10ByOrderByDataCriacaoDesc();
    }

    // seg min hr dia mês dia da semana
    // */10 = a cada dez minutos
    @Scheduled(cron = "0 */10 * * * *")
    public void removerExpiradas() {
        fraseAnonimaRepository.deleteByDataExpiracaoBefore();
    }
}