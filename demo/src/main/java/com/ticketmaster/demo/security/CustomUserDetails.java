package com.ticketmaster.demo.security;


import com.ticketmaster.demo.model.Usuario.User;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public record CustomUserDetails(User usuario) implements UserDetails {

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Lo leemos directo del objeto usuario
        return usuario.getAuthorities();
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    @NonNull
    public String getUsername() {
        return usuario.getUsername();
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

    /*
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(usuario.getActivo()) && Boolean.TRUE.equals(usuario.getEmailVerificado());
    }*/

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(usuario.getActivo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        // Comparamos usando el ID único del usuario
        return Objects.equals(usuario.getId(), that.usuario.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario.getId());
    }
}