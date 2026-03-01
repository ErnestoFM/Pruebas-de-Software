package com.ticketmaster.demo.controller;

import com.ticketmaster.demo.dto.ForgotPasswordRequest;
import com.ticketmaster.demo.dto.ResetPasswordRequest;
import com.ticketmaster.demo.dto.RestResponse;
import com.ticketmaster.demo.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/forgot")
    public ResponseEntity<RestResponse<Void>> forgot(@Valid @RequestBody ForgotPasswordRequest body) {
        passwordRecoveryService.solicitarRecuperacion(body.email());
        return ResponseEntity.ok(RestResponse.success("Si el correo existe y está verificado, enviaremos un enlace de recuperación"));
    }

    @PostMapping("/reset")
    public ResponseEntity<RestResponse<Void>> reset(@Valid @RequestBody ResetPasswordRequest body) {
        passwordRecoveryService.resetearConToken(body.token(), body.nuevaPassword());
        return ResponseEntity.ok(RestResponse.success("Contraseña actualizada correctamente"));
    }
}