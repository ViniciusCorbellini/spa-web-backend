package com.manocorbas.dev_web_backend.dtos.frase_anonima;

import java.time.LocalDateTime;

public record CriarFraseRequest (
    String texto,
    LocalDateTime dataExpiracao
){}
