package com.ticketmaster.demo.config;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class DebugListener {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        System.out.println("✅ LOGIN ÉXITO: " + event.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        System.out.println("❌ LOGIN FALLÓ: " + event.getAuthentication().getName());
        System.out.println("🔥 CAUSA EXACTA: " + event.getException().getClass().getName());
        System.out.println("📝 MENSAJE: " + event.getException().getMessage());

        // Imprime el error completo por si hay algo escondido
        event.getException().printStackTrace();
    }
}