package com.manocorbas.dev_web_backend.controllers;

import com.manocorbas.dev_web_backend.services.PostService;
import com.manocorbas.dev_web_backend.dtos.PostResponseDto;
import com.manocorbas.dev_web_backend.models.Post;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.security.CustomUserDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@Hidden
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> criarPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String texto) {
        try {
            Usuario user = userDetails.getUsuario();
            Post post = postService.criarPost(user.getId(), texto);
            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Post>> listarPosts() {
        return ResponseEntity.ok(postService.listarPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return postService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Post>> listarPostsUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(postService.listarPostsUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/paginado")
    public ResponseEntity<Page<Post>> listarPostsUsuarioPaginado(
            @PathVariable Long usuarioId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.listarPostsUsuario(usuarioId, pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PostResponseDto>> buscarPorPalavra(@RequestParam String palavra) {
        return ResponseEntity.ok(postService.buscarPorPalavra(palavra));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam String novoTexto) {

        try {
            Long userId = userDetails.getUsuario().getId();

            PostResponseDto dto = postService.atualizarPost(id, userId, novoTexto);

            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        try {
            Long usuarioId = userDetails.getUsuario().getId();

            postService.deletarPost(id, usuarioId);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
