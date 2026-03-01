package com.ticketmaster.demo.dto;

import com.ticketmaster.demo.model.Usuario.User;

public record DtoUserResumen(
        String id,
        String userName,
        String email,
        //TODO: crear feature para fotos del Usuario
        String fotoPerfilUrl
) {
    public static DtoUserResumen of(User u, String fotoPerfilUrl) {
        if (u == null) return null;
        return new DtoUserResumen(u.getId(), u.getUsername(), u.getEmail(), fotoPerfilUrl);
    }
}