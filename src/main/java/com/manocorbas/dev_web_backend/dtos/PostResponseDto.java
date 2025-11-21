package com.manocorbas.dev_web_backend.dtos;

import java.time.LocalDateTime;

public record PostResponseDto(
    Long id,
    Long usuarioId,
    String texto,
    String nomeUsuario,
    String fotoPerfil,
    LocalDateTime dataCriacao
) {}

