package com.manocorbas.dev_web_backend.dtos;

public record AuthResponse (
    Long id,
    String nome, 
    String email,
    String token
) {}
