package com.manocorbas.dev_web_backend.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.manocorbas.dev_web_backend.dtos.Usuario.PutUsuarioRequest;
import com.manocorbas.dev_web_backend.dtos.Usuario.UsuarioCleanResponse;
import com.manocorbas.dev_web_backend.dtos.Usuario.UsuarioPublicDto;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.repositories.SeguidorRepository;
import com.manocorbas.dev_web_backend.repositories.UsuarioRepository;
import com.manocorbas.dev_web_backend.security.CustomUserDetails;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ImagemService imagemService;
    private final PasswordEncoder passwordEncoder;
    private final SeguidorRepository seguidorRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, ImagemService imagemService,
            PasswordEncoder passwordEncoder,
            SeguidorRepository seguidorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.imagemService = imagemService;
        this.passwordEncoder = passwordEncoder;
        this.seguidorRepository = seguidorRepository;
    }

    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        usuario.setDataCriacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioPublicDto> buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    long seguidores = seguidorRepository.countBySeguidoId(id);
                    long seguindo = seguidorRepository.countBySeguidorId(id);

                    return new UsuarioPublicDto(
                            usuario.getId(),
                            usuario.getNome(),
                            usuario.getFotoPerfil(),
                            seguidores,
                            seguindo);
                });

    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<UsuarioCleanResponse> buscarPorNome(String nome) {
        List<Usuario> users = usuarioRepository.findByNomeContainingIgnoreCase(nome);

        return users.stream()
                .map(usuario -> {

                    return new UsuarioCleanResponse(
                            usuario.getId(),
                            usuario.getNome(),
                            usuario.getFotoPerfil());
                })
                .toList();
    }

    @Transactional
    public Usuario atualizarUsuario(CustomUserDetails userDetails, PutUsuarioRequest novosDados,
            MultipartFile novaImagem) throws IOException {
        Usuario usuario = userDetails.getUsuario();

        // Atualiza nome, se vier preenchido
        if (novosDados.nome() != null && !novosDados.nome().isBlank()) {
            usuario.setNome(novosDados.nome());
        }

        // Atualiza email, se vier preenchido
        if (novosDados.email() != null && !novosDados.email().isBlank()) {
            usuario.setEmail(novosDados.email());
        }

        // Codifica e atualiza senha, se vier preenchida
        if (novosDados.senha() != null && !novosDados.senha().isBlank()) {
            usuario.setSenhaHash(passwordEncoder.encode(novosDados.senha()));
        }

        // Atualização da img
        if (novaImagem != null && !novaImagem.isEmpty()) {

            // remove imagem antiga se houver
            if (usuario.getFotoPerfil() != null) {
                imagemService.deletarImagem(usuario.getFotoPerfil());
            }

            // salva nova imagem e pega o caminho/URL
            String caminhoNovo = imagemService.salvarFotoPerfil(novaImagem);
            usuario.setFotoPerfil(caminhoNovo);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}