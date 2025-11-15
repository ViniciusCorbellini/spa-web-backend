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

import com.manocorbas.dev_web_backend.models.Post;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.services.RecommendationService;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getRecommendedPosts(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> recommended = recommendationService.getRecommendedPosts(usuario, pageable);

        return ResponseEntity.ok(recommended);
    }
}

