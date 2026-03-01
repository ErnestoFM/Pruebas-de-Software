package com.ticketmaster.demo.dto;

import com.ticketmaster.demo.model.Usuario.User;

public record DtoUser (
        String id,
        String userName,
        String telefono,
        String email,
        String rol
){
    public DtoUser(User usuario) {
        this(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getTelefono(),
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().name().replaceFirst("^ROLE_", "") : null
        );
    }
}
