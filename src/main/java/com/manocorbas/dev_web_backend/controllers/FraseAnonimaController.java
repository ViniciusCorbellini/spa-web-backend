package com.manocorbas.dev_web_backend.controllers;

import com.manocorbas.dev_web_backend.models.FraseAnonima;
import com.manocorbas.dev_web_backend.services.FraseAnonimaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/frases")
public class FraseAnonimaController {

    private final FraseAnonimaService fraseAnonimaService;

    public FraseAnonimaController(FraseAnonimaService fraseAnonimaService) {
        this.fraseAnonimaService = fraseAnonimaService;
    }

    @PostMapping
    public ResponseEntity<?> salvarFrase(
            @RequestParam String texto,
            @RequestParam LocalDateTime dataExpiracao) {
        try {
            FraseAnonima frase = fraseAnonimaService.salvarFrase(texto, dataExpiracao);
            return ResponseEntity.ok(frase);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<FraseAnonima>> buscarPorPalavra(@RequestParam String palavra) {
        return ResponseEntity.ok(fraseAnonimaService.buscarPorPalavra(palavra));
    }

    @GetMapping("/ultimas")
    public ResponseEntity<List<FraseAnonima>> listarUltimasFrases() {
        return ResponseEntity.ok(fraseAnonimaService.listarUltimasFrases());
    }

    @DeleteMapping("/expiradas")
    public ResponseEntity<?> removerExpiradas() {
        fraseAnonimaService.removerExpiradas();
        return ResponseEntity.noContent().build();
    }
}