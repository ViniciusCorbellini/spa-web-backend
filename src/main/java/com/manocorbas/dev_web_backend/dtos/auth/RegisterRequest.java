package com.manocorbas.dev_web_backend.dtos.auth;

public record RegisterRequest(
    String nome, 
    String email, 
    String senhaHash
) {}
