package com.ticketmaster.demo.service;


import com.ticketmaster.demo.dto.DtoActualizarPerfilRequest;
import com.ticketmaster.demo.dto.DtoUser;
import com.ticketmaster.demo.dto.DtoUserResumen;
import com.ticketmaster.demo.dto.DtoUserDetalle;
import com.ticketmaster.demo.model.ImagenPerfil;
import com.ticketmaster.demo.model.Usuario.User;
import com.ticketmaster.demo.repository.ImagenRepository;
import com.ticketmaster.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final UserRepository repositorioUsuario;
    private final ImagenRepository repositorioImagenPerfil;
    private final PerfilUsuarioService perfilUsuarioService; // nueva dependencia

    public DtoUser toDto(User usuario) {
        return new DtoUser(usuario);
    }

    public DtoUserResumen toResumen(User usuario) {
        if (usuario == null) return null;
        String foto = obtenerUrlFotoPerfil(usuario.getId());
        return DtoUserResumen.of(usuario, foto);
    }

    public Optional<DtoUserResumen> obtenerResumenPorId(String id) {
        return repositorioUsuario.findById(id).map(this::toResumen);
    }

    public Optional<DtoUserResumen> obtenerResumenPorUserNameOEmail(String userOrEmail) {
        return repositorioUsuario.findByUserNameOrEmail(userOrEmail, userOrEmail).map(this::toResumen);
    }

    // --- DTO Detalle (más campos del usuario) ---
    public Optional<DtoUserDetalle> obtenerDetallePorId(String id) {
        return perfilUsuarioService.obtenerPerfilCompleto(id);
    }

    public Optional<DtoUserDetalle> obtenerDetallePorUserName(String userName) {
        return perfilUsuarioService.obtenerPerfilCompleto(userName);
    }

    public Optional<DtoUserDetalle> obtenerDetallePorEmail(String email) {
        return Optional.ofNullable(perfilUsuarioService.obtenerPerfilCompletoEmail(email));
    }

    // Método auxiliar para recuperar la URL pública de la imagen de perfil activa
    private String obtenerUrlFotoPerfil(String usuarioId) {
        return repositorioImagenPerfil.findActivaByUsuarioId(usuarioId)
                .map(ImagenPerfil::getUrlPublica)
                .orElse(null); // El front puede manejar null como "sin foto"
    }

    public String actualizarFotoPerfil(String usuarioId, MultipartFile archivo) {
        return perfilUsuarioService.actualizarImagenPerfil(usuarioId, archivo);
    }

    // Posibles métodos adicionales futuros (crear/actualizar usuario) podrían ir aquí
    public boolean desactivarUsuario(String id) {
        return repositorioUsuario.findById(id).map(u -> {
            if (Boolean.FALSE.equals(u.getActivo())) {
                return false; // ya inactivo
            }
            u.setActivo(false);
            // Opcional: invalidar token / email verificado
            u.setToken(null);
            u.setFechaExpiracionToken(null);
            repositorioUsuario.save(u);
            return true;
        }).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}