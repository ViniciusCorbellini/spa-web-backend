package com.manocorbas.dev_web_backend.dtos.Usuario;

public record PutUsuarioResponse(
    String nome,
    String email,
    String fotoPerfil
) {}
