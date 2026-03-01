package com.ticketmaster.demo.repository;

import com.ticketmaster.demo.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);
    long deleteByUsuario_IdAndCanalAndUsadoEnIsNull(String usuarioId, VerificationToken.VerificationChannel canal);
}
