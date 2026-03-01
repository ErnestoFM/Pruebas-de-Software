package com.ticketmaster.demo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO para registro completo que incluye todos los datos necesarios
 */
public record DtoRegistroCompletoRequest(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre de usuario solo puede contener letras, números, puntos, guiones bajos y guiones medios")
        String userName,

        @Email(message = "El email debe tener un formato válido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El nombre real es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre real debe tener entre 2 y 100 caracteres")
        String nombreReal,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener exactamente 10 dígitos")
        String telefono,

        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate fechaNacimiento


) {
}
