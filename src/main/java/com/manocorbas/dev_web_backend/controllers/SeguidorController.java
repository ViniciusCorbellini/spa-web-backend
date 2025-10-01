package com.manocorbas.dev_web_backend.controllers;

import com.manocorbas.dev_web_backend.models.Seguidor;
import com.manocorbas.dev_web_backend.services.SeguidorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
@RequestMapping("/seguidores")
public class SeguidorController {

    private final SeguidorService seguidorService;

    public SeguidorController(SeguidorService seguidorService) {
        this.seguidorService = seguidorService;
    }

    @PostMapping("/seguir")
    public ResponseEntity<?> seguir(
            @RequestParam Long seguidorId,
            @RequestParam Long seguidoId) {
        try {
            Seguidor novoFollow = seguidorService.seguir(seguidorId, seguidoId);
            return ResponseEntity.ok(novoFollow);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/deixar-de-seguir")
    public ResponseEntity<?> deixarDeSeguir(
            @RequestParam Long seguidorId,
            @RequestParam Long seguidoId) {
        try {
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
            @RequestParam Long seguidorId,
            @RequestParam Long seguidoId) {
        return ResponseEntity.ok(seguidorService.isSeguindo(seguidorId, seguidoId));
    }
}
