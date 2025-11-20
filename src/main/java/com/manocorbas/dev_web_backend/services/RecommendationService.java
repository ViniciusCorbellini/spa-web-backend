package com.manocorbas.dev_web_backend.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

        // deixando o algoritmo mais inteligente:
        // agora, se o numero de posts recomendados for menor que o tamanho da página
        // (default=10)
        // ocorre uma busca de mais posts de usuarios "relevantes" (maior nº de
        // seguidores)
        int gap = pageable.getPageSize() - recommended.getContent().size();

        // lógica de fallback de recomendação
        if (gap > 0) {
            Page<Post> popularPosts = postRepository.findPopularPosts(
                    usuario.getId(), // Exclui o id do usuario da busca
                    PageRequest.of(pageable.getPageNumber(), gap)); // paginação com o gap restante

            List<Post> combined = new ArrayList<>(recommended.getContent());
            combined.addAll(popularPosts.getContent());

            return new PageImpl<>(
                    combined.stream().map(this::toPostDto).toList(),
                    pageable,
                    combined.size());
        }

        return recommended.map((this::toPostDto));
    }

    public Page<RecommendedUsuarioResponse> getRecommendedUsers(Usuario usuario, Pageable pageable) {
        Set<Long> visited = new HashSet<>();
        Set<Long> recommendedUserIds = new HashSet<>();

        dfs(usuario.getId(), visited, recommendedUserIds, 2); // profundidade 2 pra não explodir meu pc

        // remove o próprio usuário (ele não deve ver recomendação dele mesmo)
        recommendedUserIds.remove(usuario.getId());

        Page<Usuario> recommended = usuarioRepository.findByUsuarioIdIn(recommendedUserIds, pageable);

        // mesma lógica do getRecommendedPosts
        int gap = pageable.getPageSize() - recommended.getContent().size();

        // lógica de fallback de recomendação
        if (gap > 0) {
            // Exclui da busca os ids de usuários já recomendados
            List<Long> exclude = new ArrayList<>(recommendedUserIds);
            exclude.add(usuario.getId());

            // Encontra os usuarios mais populares, excluindo aqueles cujos ids estão em
            // List<Long> exclude
            Page<Usuario> popular = usuarioRepository.findMostFollowedUsers(
                    usuario.getId(),
                    exclude,
                    PageRequest.of(pageable.getPageNumber(), gap));

            // Junta os já recomendados com os posts mais populares
            List<Usuario> finalList = new ArrayList<>(recommended.getContent());
            finalList.addAll(popular.getContent());

            // Gera a página e retorna o resultado
            Page<RecommendedUsuarioResponse> result = new PageImpl<>(
                    finalList.stream().map(this::toUserDto).toList(),
                    pageable,
                    finalList.size());

            return result;
        }

        return recommended.map(this::toUserDto);
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

    private PostResponseDto toPostDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getUsuario().getId(),
                post.getTexto(),
                post.getUsuario().getNome(),
                post.getDataCriacao());
    }

    private RecommendedUsuarioResponse toUserDto(Usuario user) {
        return new RecommendedUsuarioResponse(
                user.getId(),
                user.getNome(),
                user.getFotoPerfil());
    }
}
