package com.manocorbas.dev_web_backend.repositories;

import com.manocorbas.dev_web_backend.models.Seguidor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeguidorRepository extends JpaRepository<Seguidor, Long> {

    Page<Seguidor> findBySeguidorId(Long seguidorId, Pageable pageable);

    Page<Seguidor> findBySeguidoId(Long seguidoId, Pageable pageable);

    // checa se uma relação de follow existe para otimizar botões de "Seguir/Deixar de seguir"
    boolean existsBySeguidorIdAndSeguidoId(Long seguidorId, Long seguidoId);

    // Para deixar de seguir
    void deleteBySeguidorIdAndSeguidoId(Long seguidorId, Long seguidoId);

    @Query("SELECT s.seguido.id FROM Seguidor s WHERE s.seguidor.id = :usuarioId")
    List<Long> findFollowingIdsByUsuarioId(@Param("usuarioId") Long usuarioId);
}

