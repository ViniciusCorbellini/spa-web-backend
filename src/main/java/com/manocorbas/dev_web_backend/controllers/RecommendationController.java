package com.manocorbas.dev_web_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.manocorbas.dev_web_backend.dtos.PostResponseDto;
import com.manocorbas.dev_web_backend.dtos.RecommendedUsuarioResponse;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.security.CustomUserDetails;
import com.manocorbas.dev_web_backend.services.RecommendationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponseDto>> getRecommendedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Usuario usuario = userDetails.getUsuario();

        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponseDto> recommended = recommendationService.getRecommendedPosts(usuario, pageable);

        return ResponseEntity.ok(recommended);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<RecommendedUsuarioResponse>> getRecommendedUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Usuario usuario = userDetails.getUsuario();

        Pageable pageable = PageRequest.of(page, size);
        Page<RecommendedUsuarioResponse> recommended = recommendationService.getRecommendedUsers(usuario, pageable);

        return ResponseEntity.ok(recommended);
    }
}
