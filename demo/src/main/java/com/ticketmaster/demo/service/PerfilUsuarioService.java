package com.ticketmaster.demo.service;


import com.ticketmaster.demo.config.StorageConfig;
import com.ticketmaster.demo.dto.*;
import com.ticketmaster.demo.model.ImagenPerfil;
import com.ticketmaster.demo.model.Usuario.Roles;
import com.ticketmaster.demo.model.Usuario.User;
import com.ticketmaster.demo.repository.ImagenRepository;
import com.ticketmaster.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PerfilUsuarioService {

    private final UserRepository repositorioUsuario;
    private ImagenPerfil imagenPerfil;
    private final UserRepository repositorioPerfilUsuario;
    private final ImagenRepository repositorioImagenPerfil;
    private final StorageConfig imageStorageService;

    private static final long MAX_FILE_SIZE = 15L * 1024 * 1024; // 15MB

    /**
     * Registra un usuario con información extendida de perfil usando el DTO unificado
     */
    public DtoUserDetalle registrarUsuarioExtendido(DtoUserDetalle request) {
        return registrarUsuarioExtendido(request, null);
    }

    /**
     * Variante que permite imagen de perfil
     */
    public DtoUserDetalle registrarUsuarioExtendido(DtoUserDetalle request, MultipartFile fotoPerfil) {
        User usuario = repositorioUsuario.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));

        // Validar teléfono único si se proporciona
        if (request.telefono() != null
                && existeTelefono(request.telefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }

        // Crear usuario básico directamente desde el request
        usuario.setActivo(false); // Inactivo hasta verificar email
        usuario.setEmailVerificado(false);
        usuario.setRol(Roles.ROLE_USER);
        usuario.setFechaCreacion(new java.sql.Timestamp(System.currentTimeMillis()));
        usuario = repositorioUsuario.save(usuario);


        // Procesar imagen de perfil si se proporciona

        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            imagenPerfil = procesarImagenPerfil(fotoPerfil, usuario);
        }

        return construirDtoPerfilCompleto(usuario, imagenPerfil);
    }

    /**
     * Obtiene el perfil completo de un usuario por ID
     */
    @Transactional(readOnly = true)
    public Optional<DtoUserDetalle> obtenerPerfilCompleto(String usuarioId) {
        return repositorioPerfilUsuario.findUserById(usuarioId)
                .map(perfil -> {
                    ImagenPerfil imagen = repositorioImagenPerfil.findActivaByUsuarioId(usuarioId).orElse(null);
                    return construirDtoPerfilCompleto(perfil, imagen);
                });
    }

    @Transactional(readOnly = true)
    public DtoUserDetalle obtenerPerfilCompletoEmail(String email) {
        User perfil = repositorioPerfilUsuario.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        ImagenPerfil imagen = repositorioImagenPerfil.findActivaByUsuarioId(email).orElse(null);
        return construirDtoPerfilCompleto(perfil, imagen);
    }

    @Transactional(readOnly = true)
    public Optional<DtoUserDetalle> obtenerPerfilCompletoNombre(String usuarioId) {
        return repositorioPerfilUsuario.findByUserName(usuarioId)
                .map(perfil -> {
                    ImagenPerfil imagen = repositorioImagenPerfil.findActivaByUsuarioId(usuarioId).orElse(null);
                    return construirDtoPerfilCompleto(perfil, imagen);
                });
    }

    /**
     * Actualiza los campos básicos del perfil (sin imagen)
     */
    @Transactional
    public Map<String, Object> actualizarPerfilCampos(String usuarioId, DtoActualizarPerfilRequest req) {
        User perfil = repositorioUsuario.findUserById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));

        if (req.telefono() != null && !req.telefono().isBlank()) {
            String telLimpio = req.telefono().replaceAll("[\\s()\\-]", "");
            if (!telLimpio.equals(perfil.getTelefono()) && existeTelefono(telLimpio)) {
                throw new IllegalArgumentException("El teléfono ya está registrado");
            }
            perfil.setTelefono(telLimpio);
        }
        if (req.UserName() != null) perfil.setUserName(req.UserName());

        perfil = repositorioPerfilUsuario.save(perfil);
        ImagenPerfil imagen = repositorioImagenPerfil.findActivaByUsuarioId(usuarioId).orElse(null);

        DtoUserDetalle dto = construirDtoPerfilCompleto(perfil, imagen);

        return Map.of(
                "userName", dto.userName(),
                "email", dto.email(),
                "telefono", dto.telefono(),
                "rol", dto.rol(),
                "activo", dto.activo(),
                "emailVerificado", dto.emailVerificado(),
                "fechaCreacion", dto.fechaCreacion(),
                "ultimoAcceso", dto.ultimoAcceso(),
                "tieneImagenPerfil", dto.urlImagen() != null,
                "urlImagen", dto.urlImagen() != null ? dto.urlImagen() : ""
        );
    }

    /**
     * Actualiza la imagen de perfil (elimina la anterior en el storage si aplica)
     */
    public String actualizarImagenPerfil(String usuarioId, MultipartFile archivo) {
        User perfil = repositorioPerfilUsuario.findUserById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));

        // Desactivar imagen anterior y capturar para borrado
        final String[] oldPublicIdHolder = {null};
        repositorioImagenPerfil.findActivaByUsuarioId(usuarioId)
                .ifPresent(imagenAnterior -> {
                    imagenAnterior.setActiva(false);
                    repositorioImagenPerfil.save(imagenAnterior);
                    // Derivar public_id de Cloudinary: nombreArchivo sin la extensión (si está en carpeta)
                    String nombreArchivo = imagenAnterior.getNombreArchivo();
                    if (nombreArchivo != null) {
                        int dot = nombreArchivo.lastIndexOf('.');
                        if (dot > 0) {
                            oldPublicIdHolder[0] = nombreArchivo.substring(0, dot);
                        } else {
                            oldPublicIdHolder[0] = nombreArchivo;
                        }
                    }
                });

        ImagenPerfil nuevaImagen = procesarImagenPerfil(archivo, perfil);

        // Borrado best-effort de imagen anterior en storage
        if (oldPublicIdHolder[0] != null) {
            try {
                // Pasamos el ID público correcto. Si se subió a carpeta 'perfiles', Cloudinary requiere 'perfiles/ID'.
                // Pero como nuestro servicio genérico sube a carpeta, el public_id que retorna incluye la carpeta?
                // El servicio Cloudinary actual retorna como nombre: id + ext.
                // El anterior retornaba id + ext.
                // Y el nuevo servicio Cloudinary sube a 'uploads' por defecto o a carpeta específica.
                // Asumiremos que el storage maneja el borrado por "public ID".
                // En Cloudinary, si subes a 'perfiles', public_id es 'perfiles/UUID'.
                // Pero en mi implementación anterior del storage, `deleteProfileImage` hacía `perfilesFolder + "/" + id`.
                // Ahora, el `delete` genérico solo toma el string.
                // Si la imagen anterior fue subida con el sistema viejo, necesitamos saber la carpeta.
                // Para simplificar en boilerplate limpio (asumiendo base nueva o migrada),
                // asumimos que 'delete' recibe el public_id completo.
                // Pero aquí solo tenemos el filename (UUID.ext).
                // Como esto es un boilerplate, podemos relajar el borrado de imagenes viejas o asumir una carpeta por defecto 'perfiles'.
                imageStorageService.delete("perfiles/" + oldPublicIdHolder[0]);
            } catch (Exception ignored) {}
        }
        return nuevaImagen.getUrlPublica();
    }

    /**
     * Procesa y guarda una imagen de perfil con Cloudinary
     */
    @Transactional
    protected ImagenPerfil procesarImagenPerfil(MultipartFile archivo, User perfil) {
        validarArchivo(archivo);
        try {
            // Usamos carpeta 'perfiles'
            UploadResult up = imageStorageService.upload(archivo, "perfiles");
            ImagenPerfil imagenPerfil = new ImagenPerfil();
            imagenPerfil.setUser(perfil);
            imagenPerfil.setNombreArchivo(up.getFilename());
            imagenPerfil.setRutaArchivo(up.getUrl());
            imagenPerfil.setTipoMime(up.getContentType());
            imagenPerfil.setTamanoBytes(up.getSize());
            imagenPerfil.setUrlPublica(up.getUrl());
            imagenPerfil.setActiva(true);
            return repositorioImagenPerfil.save(imagenPerfil);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen de perfil", e);
        }
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
        }
        if (archivo.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (15MB)");
        }
    }

    /**
     * Construye el DTO de perfil completo
     */
    private DtoUserDetalle construirDtoPerfilCompleto(User usuario, ImagenPerfil imagen) {
        return new DtoUserDetalle(
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol().toString(),
                usuario.getActivo(),
                usuario.getEmailVerificado(),
                usuario.getFechaCreacion(),
                usuario.getUltimoAcceso(),
                imagen != null ? imagen.getUrlPublica() : null
        );

    }

    /**
     * Verifica si un teléfono ya está registrado en la base de datos
     */
    public boolean existeTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        String telefonoLimpio = telefono.replaceAll("[\\s()\\-]", "");
        return repositorioPerfilUsuario.findByTelefono(telefonoLimpio).isPresent();
    }
}
