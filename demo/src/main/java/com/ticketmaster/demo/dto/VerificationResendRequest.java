package com.ticketmaster.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerificationResendRequest(
        @NotBlank String canal,
        @NotBlank @Email String destino
) {}

