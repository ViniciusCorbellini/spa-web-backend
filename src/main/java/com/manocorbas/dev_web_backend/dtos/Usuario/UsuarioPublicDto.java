package com.manocorbas.dev_web_backend.dtos.Usuario;

public record UsuarioPublicDto(
        Long id,
        String nome,
        long seguidores,
        long seguindo
) {}

