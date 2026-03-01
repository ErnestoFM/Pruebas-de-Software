package com.ticketmaster.demo.validaciones;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FormatoUsuarioValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FormatoUsuario {
    String message() default "El formato de usuario es inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}