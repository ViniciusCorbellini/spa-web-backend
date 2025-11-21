package com.manocorbas.dev_web_backend.services;

import com.manocorbas.dev_web_backend.dtos.SeguirResponse;
import com.manocorbas.dev_web_backend.dtos.Usuario.UsuarioCleanResponse;
import com.manocorbas.dev_web_backend.models.Seguidor;
import com.manocorbas.dev_web_backend.models.Usuario;
import com.manocorbas.dev_web_backend.repositories.SeguidorRepository;
import com.manocorbas.dev_web_backend.repositories.UsuarioRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SeguidorService {

    private final SeguidorRepository seguidorRepository;
    private final UsuarioRepository usuarioRepository; // pra validar se usuário existe

    public SeguidorService(SeguidorRepository seguidorRepository, UsuarioRepository usuarioRepository) {
        this.seguidorRepository = seguidorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public SeguirResponse seguir(Long seguidorId, Long seguidoId) {
        if (seguidorId.equals(seguidoId)) {
            throw new IllegalArgumentException("Um usuário não pode seguir a si mesmo");
        }

        if (seguidorRepository.existsBySeguidorIdAndSeguidoId(seguidorId, seguidoId)) {
            throw new IllegalStateException("Já existe relação de follow entre esses usuários");
        }

        Usuario seguidor = usuarioRepository.findById(seguidorId)
                .orElseThrow(() -> new IllegalArgumentException("Seguidor não encontrado"));
        Usuario seguido = usuarioRepository.findById(seguidoId)
                .orElseThrow(() -> new IllegalArgumentException("Seguido não encontrado"));

        Seguidor novoFollow = new Seguidor();
        novoFollow.setSeguidor(seguidor);
        novoFollow.setSeguido(seguido);

        Seguidor salvo = seguidorRepository.save(novoFollow);

        return new SeguirResponse(
                salvo.getId(),
                new UsuarioCleanResponse(seguidor.getId(), seguidor.getNome(), seguidor.getFotoPerfil()),
                new UsuarioCleanResponse(seguido.getId(), seguido.getNome(), seguido.getFotoPerfil()));
    }

    @Transactional
    public void deixarDeSeguir(Long seguidorId, Long seguidoId) {
        if (!seguidorRepository.existsBySeguidorIdAndSeguidoId(seguidorId, seguidoId)) {
            throw new IllegalStateException("Relação de follow não existe");
        }
        seguidorRepository.deleteBySeguidorIdAndSeguidoId(seguidorId, seguidoId);
    }

    @Transactional(readOnly = true)
    public Page<Seguidor> listarSeguindo(Long seguidorId, Pageable pageable) {
        return seguidorRepository.findBySeguidorId(seguidorId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Seguidor> listarSeguidores(Long seguidoId, Pageable pageable) {
        return seguidorRepository.findBySeguidoId(seguidoId, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isSeguindo(Long seguidorId, Long seguidoId) {
        return seguidorRepository.existsBySeguidorIdAndSeguidoId(seguidorId, seguidoId);
    }
}
