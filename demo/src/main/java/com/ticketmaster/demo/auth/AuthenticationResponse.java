package com.ticketmaster.demo.auth;
import com.ticketmaster.demo.dto.DtoUser;
public record AuthenticationResponse(
        String token,
        String refreshToken,
        DtoUser usuario,
        long expiresIn,
        boolean success,
        String mensaje
) {
    // Constructor para login exitoso
    public AuthenticationResponse(String token, String refreshToken, DtoUser usuario, long expiresIn) {
        this(token, refreshToken, usuario, expiresIn, true, "Inicio de sesión exitoso");
    }

    // Constructor para refresh exitoso
    public static AuthenticationResponse refreshSuccess(String token, String refreshToken, DtoUser usuario, long expiresIn) {
        return new AuthenticationResponse(token, refreshToken, usuario, expiresIn, true, "Sesión renovada exitosamente");
    }
}