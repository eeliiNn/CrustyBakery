package com.crustybakery.web.security;

import com.crustybakery.web.dto.UsuarioDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador entre el UsuarioDto que devuelve la API y lo que Spring Security
 * necesita para manejar la sesion autenticada.
 */
public class CustomUserDetails implements UserDetails {

    private final UsuarioDto usuario;

    public CustomUserDetails(UsuarioDto usuario) {
        this.usuario = usuario;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public Integer getId() {
        return usuario.getId();
    }

    public String getNombre() {
        return usuario.getNombre();
    }

    public String getRol() {
        return usuario.getRol();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
    }

    @Override
    public String getPassword() {
        // La contrasena nunca se guarda en el lado Java; la validacion ocurre en la API.
        return "";
    }

    @Override
    public String getUsername() {
        // Spring Security exige un "username", pero en nuestro esquema el
        // usuario se identifica por correo.
        return usuario.getCorreo();
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
        return usuario.isActivo();
    }
}
