package com.ticketmaster.demo.dto;

import com.ticketmaster.demo.model.Usuario.User;

import java.sql.Timestamp;

public record DtoUserDetalle(
        String userName,
        String email,
        String telefono,
        String rol,
        Boolean activo,
        Boolean emailVerificado,
        Timestamp fechaCreacion,
        Timestamp ultimoAcceso,
        String urlImagen
) {
    public static DtoUserDetalle from(User u) {
        if (u == null) return null;
        return new DtoUserDetalle(
                u.getUsername(),
                u.getEmail(),
                u.getTelefono(),
                u.getRol() != null ? u.getRol().name().replaceFirst("^ROLE_", "") : null,
                u.getActivo(),
                u.getEmailVerificado(),
                u.getFechaCreacion(),
                u.getUltimoAcceso(),
                u.getImagenesPerfil().toString()
        );
    }
}

