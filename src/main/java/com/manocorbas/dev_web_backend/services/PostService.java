package com.manocorbas.dev_web_backend.services;

import com.manocorbas.dev_web_backend.dtos.PostResponseDto;
import com.manocorbas.dev_web_backend.models.Post;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.repositories.PostRepository;
import com.manocorbas.dev_web_backend.repositories.UsuarioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;

    public PostService(PostRepository postRepository, UsuarioRepository usuarioRepository) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Post criarPost(Long usuarioId, String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Texto do post não pode ser vazio");
        }
        if (texto.length() > 280) {
            throw new IllegalArgumentException("Texto ultrapassa o limite de 280 caracteres");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Post post = new Post();
        post.setUsuario(usuario);
        post.setTexto(texto);

        return postRepository.save(post);
    }

    public List<Post> listarPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> buscarPorId(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> listarPostsUsuario(Long usuarioId) {
        return postRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioId);
    }

    public Page<Post> listarPostsUsuario(Long usuarioId, Pageable pageable) {
        return postRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioId, pageable);
    }

    public List<PostResponseDto> buscarPorPalavra(String palavra) {
        return postRepository.findByTextoContainingIgnoreCase(palavra)
                .stream()
                .map(post -> new PostResponseDto(
                        post.getId(),
                        post.getUsuario().getId(),
                        post.getTexto(),
                        post.getUsuario().getNome(),
                        post.getUsuario().getFotoPerfil(),
                        post.getDataCriacao()))
                .toList();
    }

    @Transactional
    public PostResponseDto atualizarPost(Long id, Long usuarioId, String novoTexto) {

        if (novoTexto == null || novoTexto.isBlank()) {
            throw new IllegalArgumentException("Texto do post não pode ser vazio");
        }

        if (novoTexto.length() > 280) {
            throw new IllegalArgumentException("Texto ultrapassa o limite de 280 caracteres");
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post não encontrado"));

        // Verificar se o dono do post é o usuario logado
        if (!post.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Você não pode atualizar um post de outro usuário");
        }

        post.setTexto(novoTexto);
        Post salvo = postRepository.save(post);

        return new PostResponseDto(
                salvo.getId(),
                salvo.getUsuario().getId(),
                salvo.getTexto(),
                salvo.getUsuario().getNome(),
                salvo.getUsuario().getFotoPerfil(),
                salvo.getDataCriacao());
    }

    @Transactional
    public void deletarPost(Long id, Long usuarioId) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post não encontrado"));

        // impedir delete de post de outra pessoa
        if (!post.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Você não pode deletar um post de outro usuário");
        }

        postRepository.delete(post);
    }

}
