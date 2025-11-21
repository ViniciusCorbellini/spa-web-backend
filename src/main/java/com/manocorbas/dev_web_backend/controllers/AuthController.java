package com.manocorbas.dev_web_backend.controllers;

import com.manocorbas.dev_web_backend.dtos.auth.AuthResponse;
import com.manocorbas.dev_web_backend.dtos.auth.LoginRequest;
import com.manocorbas.dev_web_backend.dtos.auth.RegisterRequest;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.security.CustomUserDetails;
import com.manocorbas.dev_web_backend.security.CustomUserDetailsService;
import com.manocorbas.dev_web_backend.security.JwtService;
import com.manocorbas.dev_web_backend.services.ImagemService;
import com.manocorbas.dev_web_backend.services.UsuarioService;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final ImagemService imagemService;

    public AuthController(UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService customUserDetailsService,
            ImagemService imagemService) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.imagemService = imagemService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> register(
            @RequestPart("user") RegisterRequest dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) 
    throws IOException        
    {
        Usuario user = new Usuario();
        user.setNome(dto.nome());
        user.setEmail(dto.email());
        user.setSenhaHash(passwordEncoder.encode(dto.senhaHash()));

        // Se a requisição conter uma foto válida
        if (foto != null && !foto.isEmpty()) {
            String caminhoFoto = imagemService.salvarFotoPerfil(foto);
            user.setFotoPerfil(caminhoFoto);
        }

        usuarioService.criarUsuario(user);

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getNome(), user.getEmail(), user.getFotoPerfil(), token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

        Usuario user = usuarioService.buscarPorEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getNome(), user.getEmail(), user.getFotoPerfil(), token));
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String header) {
        try {
            if (header == null || !header.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = header.substring(7);
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(token, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
