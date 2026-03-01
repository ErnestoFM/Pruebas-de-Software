package com.ticketmaster.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DtoValidacionPaso1(

        @NotBlank(message = "El nombre de usuario es requerido")
        @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre de usuario solo puede contener letras, números, puntos, guiones bajos y guiones medios")
        String userName,

        @NotBlank(message = "El email es requerido")
        @Email(message = "El formato del email no es válido")
        @Size(max = 100, message = "El email no puede exceder 100 caracteres")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 6, max = 128, message = "La contraseña debe tener entre 6 y 128 caracteres")
        String password
) {
}
