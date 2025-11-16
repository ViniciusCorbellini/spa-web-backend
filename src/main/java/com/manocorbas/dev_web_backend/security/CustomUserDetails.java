package com.manocorbas.dev_web_backend.security;

import com.manocorbas.dev_web_backend.models.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections; // Usaremos para a lista imutável

// Classe que implementa UserDetails e adapta a entidade Usuario
public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    // Autoridade Padrão que todo usuário terá
    private static final GrantedAuthority DEFAULT_AUTHORITY = new SimpleGrantedAuthority("ROLE_USER");

    // Construtor que recebe a entidade Usuario
    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    // Método principal: Fornece a autoridade padrão
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna uma lista imutável contendo apenas a autoridade padrão
        return Collections.singletonList(DEFAULT_AUTHORITY);
    }

    // Usa o campo 'senha' da sua entidade
    @Override
    public String getPassword() {
        return usuario.getSenhaHash();
    }

    // Usa o campo 'email' da sua entidade como username
    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    // Métodos de status da conta (true por padrão, a menos que você tenha lógica de
    // desativação)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}