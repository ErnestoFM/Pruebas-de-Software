package com.ticketmaster.demo.repository;

import com.ticketmaster.demo.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioResetToken extends JpaRepository<ResetToken, String> {
    Optional<ResetToken> findByToken(String token);
}

