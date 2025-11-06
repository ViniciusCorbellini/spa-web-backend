package com.manocorbas.dev_web_backend.dtos;

import com.manocorbas.dev_web_backend.models.Usuario;

public record AuthResponse (Usuario user, String token){
}
