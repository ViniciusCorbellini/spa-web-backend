package com.manocorbas.dev_web_backend.controllers;

import com.manocorbas.dev_web_backend.services.PostService;
import com.manocorbas.dev_web_backend.models.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> criarPost(
            @RequestParam Long usuarioId,
            @RequestParam String texto) {
        try {
            Post post = postService.criarPost(usuarioId, texto);
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
    public ResponseEntity<List<Post>> buscarPorPalavra(@RequestParam String palavra) {
        return ResponseEntity.ok(postService.buscarPorPalavra(palavra));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPost(
            @PathVariable Long id,
            @RequestParam String novoTexto) {
        try {
            Post atualizado = postService.atualizarPost(id, novoTexto);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPost(@PathVariable Long id) {
        try {
            postService.deletarPost(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
