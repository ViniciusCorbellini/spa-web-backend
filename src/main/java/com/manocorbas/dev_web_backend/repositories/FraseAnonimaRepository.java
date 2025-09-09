package com.manocorbas.dev_web_backend.repositories;

import com.manocorbas.dev_web_backend.models.FraseAnonima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FraseAnonimaRepository extends JpaRepository<FraseAnonima, Long> {

    List<FraseAnonima> findByTextoContainingIgnoreCase(String palavra);

    List<FraseAnonima> findTop10ByOrderByDataCriacaoDesc();

    @Modifying
    @Query(value = "SELECT remover_frases_expiradas()", nativeQuery = true)
    void deleteByDataExpiracaoBefore();
}
