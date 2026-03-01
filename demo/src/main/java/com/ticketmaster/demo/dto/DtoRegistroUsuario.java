package com.ticketmaster.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record DtoRegistroUsuario(

        // --- DATOS PERSONALES PARA ARMAR EL USUARIO (CURP) ---

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre es demasiado largo")
        String nombre,

        // El segundo nombre es opcional, por lo que no lleva @NotBlank
        @Size(max = 50, message = "El segundo nombre es demasiado largo")
        String segundoNombre,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(max = 50, message = "El apellido paterno es demasiado largo")
        String apellidoPaterno,

        @NotBlank(message = "El apellido materno es obligatorio")
        @Size(max = 50, message = "El apellido materno es demasiado largo")
        String apellidoMaterno,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser una fecha en el pasado")
        LocalDate fechaNacimiento,

        @NotBlank(message = "El sexo es obligatorio")
        @Pattern(regexp = "^[HM]$", message = "El sexo debe ser H (Hombre) o M (Mujer)")
        String genero,

        @NotBlank(message = "El estado es obligatorio")
        @Size(min = 2, max = 2, message = "El estado debe ser la clave de 2 letras (Ej: JC, MI, DF)")
        String estado,

        // --- DATOS DE CUENTA Y CONTACTO ---

        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "Debe ser un email válido")
        String email,

        String confirmEmail,

        @Pattern(regexp = "^(\\d{10})?$", message = "El teléfono debe tener exactamente 10 dígitos numéricos")
        String telefono,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        // Regex actualizado para hacer match perfecto con el frontend
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8}$",
                message = "La contraseña debe tener exactamente 8 caracteres, incluir mayúscula, minúscula, número y símbolo especial.")
        String password,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String confirmPassword,

        @AssertTrue(message = "Debes aceptar los términos y condiciones")
        Boolean terms
) {
}