package com.images.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ImageRequest(
        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String description
) {
}
