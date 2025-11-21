package com.manocorbas.dev_web_backend.dtos.auth;

public record LoginRequest (
    String email, 
    String senha
){}
