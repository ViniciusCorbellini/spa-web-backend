package com.manocorbas.dev_web_backend.dtos;

import com.manocorbas.dev_web_backend.dtos.Usuario.UsuarioCleanResponse;

public record SeguirResponse(
        Long id,
        UsuarioCleanResponse seguidor,
        UsuarioCleanResponse seguido
) {}
