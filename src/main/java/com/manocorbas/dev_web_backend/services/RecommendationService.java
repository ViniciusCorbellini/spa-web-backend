package com.manocorbas.dev_web_backend.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.manocorbas.dev_web_backend.dtos.PostResponseDto;
import com.manocorbas.dev_web_backend.models.Post;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.repositories.PostRepository;
import com.manocorbas.dev_web_backend.repositories.SeguidorRepository;

@Service
public class RecommendationService {

    private final PostRepository postRepository;
    private final SeguidorRepository seguidorRepository;

    public RecommendationService(PostRepository postRepository, SeguidorRepository seguidorRepository) {
        this.postRepository = postRepository;
        this.seguidorRepository = seguidorRepository;
    }

    public Page<PostResponseDto> getRecommendedPosts(Usuario usuario, Pageable pageable) {
        Set<Long> visited = new HashSet<>();
        Set<Long> recommendedUserIds = new HashSet<>();
        
        dfs(usuario.getId(), visited, recommendedUserIds, 2); // profundidade 2 pra não explodir
        
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
            )
        );

        return dtoPage;
    }

    private void dfs(Long userId, Set<Long> visited, Set<Long> result, int depth) {
        if (depth == 0 || visited.contains(userId)) return;

        visited.add(userId);

        List<Long> followingIds = seguidorRepository.findFollowingIdsByUsuarioId(userId);
        result.addAll(followingIds);

        for (Long followingId : followingIds) {
            dfs(followingId, visited, result, depth - 1);
        }
    }
}
