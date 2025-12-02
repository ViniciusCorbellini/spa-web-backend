package com.manocorbas.dev_web_backend.controllers;

import com.manocorbas.dev_web_backend.dtos.frase_anonima.CriarFraseRequest;
import com.manocorbas.dev_web_backend.dtos.frase_anonima.FraseAnonimaDto;
import com.manocorbas.dev_web_backend.models.FraseAnonima;
import com.manocorbas.dev_web_backend.security.CustomUserDetails;
import com.manocorbas.dev_web_backend.services.FraseAnonimaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/frases")
public class FraseAnonimaController {

    private final FraseAnonimaService fraseAnonimaService;

    public FraseAnonimaController(FraseAnonimaService fraseAnonimaService) {
        this.fraseAnonimaService = fraseAnonimaService;
    }

    @PostMapping
    public ResponseEntity<?> salvarFrase(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CriarFraseRequest request
        ) {
        try {
            FraseAnonimaDto frase = fraseAnonimaService.salvarFrase(request.texto(), userDetails.getUsuario(), request.dataExpiracao());
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
}