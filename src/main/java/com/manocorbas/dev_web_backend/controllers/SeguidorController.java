package com.manocorbas.dev_web_backend.controllers;

import com.manocorbas.dev_web_backend.dtos.SeguirResponse;
import com.manocorbas.dev_web_backend.models.Seguidor;
import com.manocorbas.dev_web_backend.security.CustomUserDetails;
import com.manocorbas.dev_web_backend.services.SeguidorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/seguidores")
public class SeguidorController {

    private final SeguidorService seguidorService;

    public SeguidorController(SeguidorService seguidorService) {
        this.seguidorService = seguidorService;
    }

    @PostMapping("/seguir")
    public ResponseEntity<?> seguir(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long seguidoId) {
        try {
            Long seguidorId = userDetails.getUsuario().getId();
            SeguirResponse novoFollow = seguidorService.seguir(seguidorId, seguidoId);
            return ResponseEntity.ok(novoFollow);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/deixar-de-seguir")
    public ResponseEntity<?> deixarDeSeguir(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long seguidoId) {
        try {
            Long seguidorId = userDetails.getUsuario().getId();
            seguidorService.deixarDeSeguir(seguidorId, seguidoId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/seguindo/{seguidorId}")
    public ResponseEntity<Page<Seguidor>> listarSeguindo(
            @PathVariable Long seguidorId,
            Pageable pageable) {
        return ResponseEntity.ok(seguidorService.listarSeguindo(seguidorId, pageable));
    }

    @GetMapping("/seguidores/{seguidoId}")
    public ResponseEntity<Page<Seguidor>> listarSeguidores(
            @PathVariable Long seguidoId,
            Pageable pageable) {
        return ResponseEntity.ok(seguidorService.listarSeguidores(seguidoId, pageable));
    }

    @GetMapping("/is-seguindo")
    public ResponseEntity<Boolean> isSeguindo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long seguidoId) {
        Long seguidorId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(seguidorService.isSeguindo(seguidorId, seguidoId));
    }
}
