package com.ticketmaster.demo.controller;


import com.ticketmaster.demo.dto.EmailVerificationConfirmRequest;
import com.ticketmaster.demo.dto.EmailVerificationRequest;
import com.ticketmaster.demo.dto.RestResponse;
import com.ticketmaster.demo.dto.VerificationResendRequest;
import com.ticketmaster.demo.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/email/request")
    public ResponseEntity<RestResponse<Void>> requestEmail(@Valid @RequestBody EmailVerificationRequest body) {
        verificationService.requestEmailVerification(body.email());
        return ResponseEntity.ok(RestResponse.success("Si existe una cuenta con ese correo, enviaremos un enlace de verificación"));
    }

    @PostMapping("/email/confirm")
    public ResponseEntity<RestResponse<Void>> confirmEmail(@Valid @RequestBody EmailVerificationConfirmRequest body) {
        verificationService.confirmEmail(body.token());
        return ResponseEntity.ok(RestResponse.success("Correo verificado. Ya puedes iniciar sesión"));
    }

    @PostMapping("/resend")
    public ResponseEntity<RestResponse<Void>> resend(@Valid @RequestBody VerificationResendRequest body) {
        if (!"EMAIL".equalsIgnoreCase(body.canal())) {
            return ResponseEntity.badRequest().body(RestResponse.error("Canal no soportado en esta versión"));
        }
        verificationService.resendEmail(body.destino());
        return ResponseEntity.ok(RestResponse.success("Si es posible, reenviamos el enlace de verificación"));
    }
}

