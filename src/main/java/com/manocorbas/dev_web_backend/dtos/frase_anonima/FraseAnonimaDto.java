package com.manocorbas.dev_web_backend.dtos.frase_anonima;

import java.time.LocalDateTime;

public record FraseAnonimaDto(
        Long id,
        String texto,
        LocalDateTime dataCriacao,
        LocalDateTime dataExpiracao
) {}
