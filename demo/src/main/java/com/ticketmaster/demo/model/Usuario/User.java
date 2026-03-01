package com.ticketmaster.demo.model.Usuario;

import com.ticketmaster.demo.dto.DtoRegistroUsuario;
import com.ticketmaster.demo.model.ImagenPerfil;
import com.ticketmaster.demo.validaciones.FormatoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.Normalizer;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "user")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "userid", columnDefinition = "CHAR(36)")
    private String id;

    // --- NUEVOS CAMPOS DE DATOS PERSONALES ---
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "segundo_nombre", length = 50)
    private String segundoNombre;

    @Column(name = "apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 1, nullable = false)
    private String genero;

    @Column(name = "estado", length = 2, nullable = false)
    private String estado;
    // ------------------------------------------

    // El userName ahora será nuestra CURP de 20 caracteres generada
    @Column(name = "user_name", unique = true, nullable = false, length = 20)
    @FormatoUsuario
    private String userName;

    @Pattern(regexp = "^(\\+?\\d{1,4}[\\s\\-]?)?\\d{10,14}$", message = "Formato de teléfono inválido. Ejemplo: +52 1234567890 o 1234567890")
    @Column(name = "telefono", length = 20, nullable = true)
    private String telefono;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "fecha_creacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fechaCreacion;

    @Column(name = "ultimo_acceso")
    private Timestamp ultimoAcceso;

    @Column(name = "activo", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean activo = true;

    @Column(name = "email_verificado", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean emailVerificado;

    @Column(name = "token")
    private String token;

    @Column(name = "fecha_expiracion_token")
    private Timestamp fechaExpiracionToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImagenPerfil> imagenesPerfil;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private Roles rol;

    // =========================================================================
    // CONSTRUCTOR PRINCIPAL (Aquí ocurre la magia)
    // =========================================================================
    public User(DtoRegistroUsuario dto, PasswordEncoder passwordEncoder) {
        // Asignamos los datos puros
        this.nombre = dto.nombre();
        this.segundoNombre = dto.segundoNombre() != null ? dto.segundoNombre().trim() : "";        this.apellidoPaterno = dto.apellidoPaterno();
        this.apellidoMaterno = dto.apellidoMaterno();
        this.fechaNacimiento = dto.fechaNacimiento();
        this.genero = dto.genero();
        this.estado = dto.estado();
        if (dto.telefono() != null && !dto.telefono().trim().isEmpty()) {
            // Si el usuario sí escribió algo, le quitamos los espacios (ej. "123 456" -> "123456")
            this.telefono = dto.telefono().replaceAll("\\s+", "");
        } else {
            // Si lo dejó en blanco, le pasamos un 'null' limpio a la base de datos
            this.telefono = null;
        }
        // Limpieza básica
        this.email = dto.email() != null ? dto.email().trim().toLowerCase() : null;
        this.password = passwordEncoder.encode(dto.password());

        // Configuraciones de cuenta
        this.activo = true;
        this.emailVerificado = false;
        this.rol = Roles.ROLE_USER;
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());

        this.userName = generarSuperCurp();
        // 👇 PEGA ESTO JUSTO AL FINAL DE TU CONSTRUCTOR 👇
        System.out.println("\n====== 🚨 DEBUGEANDO EL REGISTRO 🚨 ======");
        System.out.println("1. Email recibido: [" + this.email + "]");
        System.out.println("2. Estado recibido: [" + this.estado + "]");
        System.out.println("3. Género recibido: [" + this.genero + "]");
        System.out.println("4. CURP GENERADA: [" + this.userName + "]");
        System.out.println("5. ¿Cumple 20 letras?: " + (this.userName.length() == 20 ? "SÍ ✅" : "NO ❌ (" + this.userName.length() + ")"));
        System.out.println("=========================================\n");
    }

    // =========================================================================
    // LÓGICA PRIVADA PARA GENERAR EL USUARIO (CURP)
    // =========================================================================
    private String generarSuperCurp() {
        String ap = limpiarTexto(this.apellidoPaterno);
        String am = limpiarTexto(this.apellidoMaterno);
        String nom = limpiarTexto(this.nombre);

        // 1. [4 Letras] Iniciales
        char l1 = ap.charAt(0);
        char l2 = primeraVocalInterna(ap);
        char l3 = am.equals("X") ? 'X' : am.charAt(0);
        char l4 = nom.charAt(0);
        String iniciales = "" + l1 + l2 + l3 + l4;

        // 2. [6 Números] Fecha AAMMDD
        String fecha = this.fechaNacimiento.format(DateTimeFormatter.ofPattern("yyMMdd"));

        // 3. [1 Letra] Sexo
        String sex = this.genero.toUpperCase();

        // 4. [2 Letras] Estado Original
        String edo = this.estado.toUpperCase();

        // 5. [3 Letras] Consonantes internas
        char c1 = primeraConsonanteInterna(ap);
        char c2 = am.equals("X") ? 'X' : primeraConsonanteInterna(am);
        char c3 = primeraConsonanteInterna(nom);
        String consonantes = "" + c1 + c2 + c3;

        // 6. [2 Letras] El Estado extra (Requisito de negocio TicketMaster)
        String edoExtra = this.estado.toUpperCase();

        // 7. [2 Números] Dígitos verificadores para llegar a los 20 caracteres y evitar duplicados exactos
        String digitosFinales = String.format("%02d", new Random().nextInt(100));

        // Ensamblado final que cumple con el Regex: ^[A-Z]{4}\d{6}[A-Z][A-Z]{2}[A-Z0-9]{3}[A-Z]{2}\d{2}$
        return (iniciales + fecha + sex + edo + consonantes + edoExtra + digitosFinales).toUpperCase();
    }

    // --- Helpers de manipulación de texto ---

    private String limpiarTexto(String input) {
        if (input == null || input.trim().isEmpty()) return "X";
        // Quita acentos y espacios
        String normalizado = Normalizer.normalize(input.trim().toUpperCase(), Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}", "").replaceAll("[^A-Z]", "");
    }

    private char primeraVocalInterna(String texto) {
        for (int i = 1; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
                return c;
            }
        }
        return 'X'; // Fallback por si no hay vocales
    }

    private char primeraConsonanteInterna(String texto) {
        for (int i = 1; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c != 'A' && c != 'E' && c != 'I' && c != 'O' && c != 'U') {
                return c;
            }
        }
        return 'X'; // Fallback por si no hay consonantes
    }

    // =========================================================================
    // METODOS DE USERDETAILS Y JPA
    // =========================================================================

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.toString()));
    }

    @Override
    @NonNull
    public String getUsername() {
        return userName; // El userName ya será la Súper CURP
    }

    @Override
    public boolean isEnabled() {
        return activo != null && activo;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User usuario = (User) o;
        return getId() != null && Objects.equals(getId(), usuario.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @PrePersist
    private void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.fechaCreacion == null) {
            this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        }
        if (this.activo == null) {
            this.activo = false; // default inactivo hasta verificar
        }
        if (this.emailVerificado == null) {
            this.emailVerificado = false;
        }
        if (this.rol == null) {
            this.rol = Roles.ROLE_USER;
        }
    }
}