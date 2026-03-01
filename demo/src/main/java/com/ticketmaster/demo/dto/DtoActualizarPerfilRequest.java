package com.ticketmaster.demo.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record DtoActualizarPerfilRequest(
        @Size(max = 100, message = "Nombre real demasiado largo")
        String UserName,
        @Pattern(regexp = "^$|^[+0-9()\\s-]{5,20}$", message = "Formato de teléfono inválido")
        String telefono
) {}
