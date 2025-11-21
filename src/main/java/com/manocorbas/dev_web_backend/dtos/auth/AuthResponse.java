package com.manocorbas.dev_web_backend.dtos.auth;

public record AuthResponse (
    Long id,
    String nome, 
    String email,
    String fotoPerfil,
    String token
) {}
