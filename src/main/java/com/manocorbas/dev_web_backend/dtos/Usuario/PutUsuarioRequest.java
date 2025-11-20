package com.manocorbas.dev_web_backend.dtos.Usuario;

public record PutUsuarioRequest(
    String nome, 
    String email,
    String senha
) {}