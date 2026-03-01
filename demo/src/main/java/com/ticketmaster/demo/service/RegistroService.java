package com.ticketmaster.demo.service;

import com.ticketmaster.demo.dto.*;
import com.ticketmaster.demo.exception.ValidacionDeIntegridad;
import com.ticketmaster.demo.model.Usuario.Roles;
import com.ticketmaster.demo.model.Usuario.User;
import com.ticketmaster.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;

import java.sql.Timestamp;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private final UserRepository repositorioUsuario;
    private final PasswordEncoder passwordEncoder;
    private final PerfilUsuarioService perfilUsuarioService;
    private final VerificationService verificationService;

    public DtoUserDetalle registroCompleto(DtoRegistroCompletoRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        validarUnicos(request.userName(), normalizedEmail, request.telefono());
        // Usar directamente el DTO unificado
        DtoUserDetalle perfil = perfilUsuarioService.registrarUsuarioExtendido(
                new DtoUserDetalle(
                        request.userName(),
                        normalizedEmail,
                        request.telefono(),
                        Roles.ROLE_USER.toString(),
                        true,
                        false,
                        Timestamp.from(Instant.now()),
                        Timestamp.from(Instant.now()),
                        null
                )
        );
        // Disparar verificación de email
        verificationService.requestEmailVerification(normalizedEmail);
        return perfil;
    }

    public DtoUserDetalle registroCompletoConImagen(DtoRegistroCompletoRequest request, MultipartFile fotoPerfil) {
        String normalizedEmail = normalizeEmail(request.email());
        validarUnicos(request.userName(), normalizedEmail, request.telefono());
        // Usar directamente el DTO unificado con imagen
        DtoUserDetalle perfil = perfilUsuarioService.registrarUsuarioExtendido(
                new DtoUserDetalle(
                        request.userName(),
                        normalizedEmail,
                        request.telefono(),
                        Roles.ROLE_USER.toString(),
                        true,
                        false,
                        Timestamp.from(Instant.now()),
                        Timestamp.from(Instant.now()),
                        null
                ),
                fotoPerfil
        );
        verificationService.requestEmailVerification(normalizedEmail);
        return perfil;
    }

    public void validarPaso1(DtoValidacionPaso1 request) {
        validarUnicos(request.userName(), normalizeEmail(request.email()), null);
    }

    public void validarPaso2(DtoValidacionPaso2 request) {
        if (perfilUsuarioService.existeTelefono(request.telefono())) {
            throw new ValidacionDeIntegridad("Este número de teléfono ya está registrado");
        }
    }

    public User crearUsuarioBasico(DtoRegistroUsuario registroUsuario) {
        String normalizedEmail = normalizeEmail(registroUsuario.email());

        User usuario = new User(registroUsuario, passwordEncoder);

        usuario.setEmail(normalizedEmail);

        // 3. Validamos que no existan duplicados en la base de datos.
        validarUnicos(usuario.getUsername(), normalizedEmail, null);

        // 4. Guardamos en la base de datos
        usuario = repositorioUsuario.save(usuario);

        // 5. Disparamos el proceso de verificación por correo
        verificationService.requestEmailVerification(normalizedEmail);

        return usuario;
    }

    private void validarUnicos(String userName, String email, String telefono) {
        if (repositorioUsuario.findByUserName(userName).isPresent()) {
            throw new ValidacionDeIntegridad("El nombre de usuario ya está en uso");
        }
        if (repositorioUsuario.findByEmail(email).isPresent()) {
            throw new ValidacionDeIntegridad("El email ya está registrado");
        }
        if (telefono != null && perfilUsuarioService.existeTelefono(telefono)) {
            throw new ValidacionDeIntegridad("Este número de teléfono ya está registrado");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
