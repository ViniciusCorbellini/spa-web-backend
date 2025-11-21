package com.manocorbas.dev_web_backend.dtos.Usuario;

public record UsuarioPublicDto(
        Long id,
        String nome,
        String fotoPerfil,
        long seguidores,
        long seguindo
) {}

