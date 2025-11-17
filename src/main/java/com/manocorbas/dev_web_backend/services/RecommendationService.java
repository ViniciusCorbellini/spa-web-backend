package com.manocorbas.dev_web_backend.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.manocorbas.dev_web_backend.dtos.PostResponseDto;
import com.manocorbas.dev_web_backend.dtos.RecommendedUsuarioResponse;
import com.manocorbas.dev_web_backend.models.Post;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.repositories.PostRepository;
import com.manocorbas.dev_web_backend.repositories.SeguidorRepository;
import com.manocorbas.dev_web_backend.repositories.UsuarioRepository;

@Service
public class RecommendationService {

    private final PostRepository postRepository;
    private final SeguidorRepository seguidorRepository;
    private final UsuarioRepository usuarioRepository;

    public RecommendationService(PostRepository postRepository, SeguidorRepository seguidorRepository,
            UsuarioRepository usuarioRepository) {
        this.postRepository = postRepository;
        this.seguidorRepository = seguidorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<PostResponseDto> getRecommendedPosts(Usuario usuario, Pageable pageable) {
        Set<Long> visited = new HashSet<>();
        Set<Long> recommendedUserIds = new HashSet<>();

        dfs(usuario.getId(), visited, recommendedUserIds, 2); // profundidade 2 pra não explodir meu pc

        // remove o próprio usuário (ele não deve ver recomendação dele mesmo)
        recommendedUserIds.remove(usuario.getId());

        Page<Post> recommended = postRepository.findByUsuarioIdIn(recommendedUserIds, pageable);

        Page<PostResponseDto> dtoPage = recommended.map(
                post -> new PostResponseDto(
                        post.getId(),
                        post.getUsuario().getId(),
                        post.getTexto(),
                        post.getUsuario().getNome(),
                        post.getDataCriacao()
        ));

        return dtoPage;
    }

    public Page<RecommendedUsuarioResponse> getRecommendedUsers(Usuario usuario, Pageable pageable) {
        Set<Long> visited = new HashSet<>();
        Set<Long> recommendedUserIds = new HashSet<>();

        dfs(usuario.getId(), visited, recommendedUserIds, 2); // profundidade 2 pra não explodir meu pc

        // remove o próprio usuário (ele não deve ver recomendação dele mesmo)
        recommendedUserIds.remove(usuario.getId());

        Page<Usuario> recommended = usuarioRepository.findByUsuarioIdIn(recommendedUserIds, pageable);

        Page<RecommendedUsuarioResponse> dtoPage = recommended.map(
                user -> new RecommendedUsuarioResponse(        
                        user.getId(),
                        user.getNome(),
                        user.getFotoPerfil()
        ));

        return dtoPage;
    }

    private void dfs(Long userId, Set<Long> visited, Set<Long> result, int depth) {
        if (depth == 0 || visited.contains(userId))
            return;

        visited.add(userId);

        List<Long> followingIds = seguidorRepository.findFollowingIdsByUsuarioId(userId);
        result.addAll(followingIds);

        for (Long followingId : followingIds) {
            dfs(followingId, visited, result, depth - 1);
        }
    }
}
