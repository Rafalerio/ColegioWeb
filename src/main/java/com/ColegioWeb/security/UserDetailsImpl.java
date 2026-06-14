package com.ColegioWeb.security;

import com.ColegioWeb.models.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;
    private boolean enabled = true;
    private String forcedRole = null;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    public UserDetailsImpl(Usuario usuario, boolean enabled) {
        this.usuario = usuario;
        this.enabled = enabled;
    }

    public UserDetailsImpl(Usuario usuario, boolean enabled, String forcedRole) {
        this.usuario = usuario;
        this.enabled = enabled;
        this.forcedRole = forcedRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = (forcedRole != null) ? forcedRole : usuario.getTipoUsuario();
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail(); // E-mail será o username de login
    }

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
        return enabled;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
