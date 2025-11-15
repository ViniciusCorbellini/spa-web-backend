package com.manocorbas.dev_web_backend.repositories;

import com.manocorbas.dev_web_backend.models.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);

    List<Post> findByTextoContainingIgnoreCase(String palavra);

    Page<Post> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.usuario.id IN :ids ORDER BY p.dataCriacao DESC")
    Page<Post> findByUsuarioIdIn(@Param("ids") Set<Long> ids, Pageable pageable);
}