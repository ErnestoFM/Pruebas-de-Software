package com.ticketmaster.demo.repository;

import com.ticketmaster.demo.model.ImagenPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImagenRepository extends JpaRepository<ImagenPerfil, String> {

    Optional<ImagenPerfil> findByPerfilUsuarioId(String perfilUsuarioId);

    Optional<ImagenPerfil> findByPerfilUsuarioIdAndActivaTrue(String perfilUsuarioId);

    // Consulta corregida usando la relación JPA correcta
    @Query("SELECT i FROM ImagenPerfil i WHERE i.user.id = :usuarioId AND i.activa = true")
    Optional<ImagenPerfil> findActivaByUsuarioId(@Param("usuarioId") String usuarioId);

    // Nueva: traer imagen + perfil + usuario para autorización
    @Query("SELECT i FROM ImagenPerfil i JOIN FETCH i.user pu JOIN FETCH pu.id u WHERE i.id = :id")
    Optional<ImagenPerfil> findByIdWithUsuario(@Param("id") String id);

    void deleteByPerfilUsuarioId(String perfilUsuarioId);
}
