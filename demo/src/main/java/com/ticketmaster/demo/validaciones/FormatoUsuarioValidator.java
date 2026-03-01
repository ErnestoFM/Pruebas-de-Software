package com.ticketmaster.demo.validaciones;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FormatoUsuarioValidator implements ConstraintValidator<FormatoUsuario, String> {

    private static final String REGEX = "^[A-Z]{4}\\d{6}[A-Z][A-Z]{2}[A-Z0-9]{3}[A-Z]{2}\\d{2}$";

    @Override
    public void initialize(FormatoUsuario constraintAnnotation) {
        // Inicialización si es necesaria
    }

    @Override
    public boolean isValid(String usuario, ConstraintValidatorContext context) {
        if (usuario == null) {
            return false; // o true si quieres permitir nulos
        }
        return usuario.matches(REGEX);
    }
}