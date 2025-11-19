package com.manocorbas.dev_web_backend.dtos;

public record PutUsuarioRequest(
    String nome, 
    String email,
    String senha
) {}