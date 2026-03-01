package com.ticketmaster.demo;

import com.ticketmaster.demo.model.Events;
import com.ticketmaster.demo.repository.EventoRepository;
import com.ticketmaster.demo.security.SecurityConfiguration;
import com.ticketmaster.demo.security.SecurityFilter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

@DataJpaTest(
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class)
        },
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
        }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(ValidationAutoConfiguration.class)
@ActiveProfiles("test")
@Disabled
class EventoRepositoryTest {

    @Autowired
    private Validator validator;
    @MockitoBean
    private SecurityFilter securityFilter;
    @Autowired
    private EventoRepository repository;
    @MockitoBean
    private SecurityConfiguration securityConfiguration;

    private Events crearEventoValido(String nombre) {
        return new Events(
                null,
                nombre,
                120, // duracion
                true, // activo
                LocalDate.now(), // fechaPublicacion
                LocalDate.now().plusDays(5), // fechaEstreno
                EnumSet.of(Events.RopaPermitida.CASUAL), // ropaPermitida
                Events.TipoEvento.CINE, // tipo
                100, // stock
                5, // maxBoletosPorUsuario
                true, // abiertoEnFestivos
                EnumSet.of(Events.DiasDisponibles.LUNES, Events.DiasDisponibles.MARTES), // diasDisponibles
                null
        );
    }

    @Test
    void debeVerificarQueElEventoSeGuardo() {
        Events evento = crearEventoValido("Concierto");
        Events guardado = repository.save(evento);

        assertThat(guardado.getId()).isNotNull();
    }

    @Test
    void debeGuardarUnEventoYEncontrarlo() {
        Events evento = crearEventoValido("Concierto Rock");
        Events guardado = repository.save(evento);

        Events founded = repository.findById(guardado.getId()).orElseThrow();
        assertEquals("Concierto Rock", founded.getNombre());
        assertEquals(100, founded.getStock());
    }

    @Test
    void debeFallarSiNombreEsNulo() {
        Events evento = crearEventoValido(null);

        Set<ConstraintViolation<Events>> violaciones = validator.validate(evento);
        assertFalse(violaciones.isEmpty());
    }

    @Test
    void debeEncontrarEventoPorNombre() {
        Events evento = crearEventoValido("Avatar");
        repository.save(evento);

        Events encontrado = repository.findByNombre("Avatar");
        assertNotNull(encontrado);
        assertEquals("Avatar", encontrado.getNombre());
    }

    @Test
    void debeFallarSiDuracionEsMenorQueUno() {
        Events evento = crearEventoValido("Duracion Invalida");
        evento.setDuracion(0);

        Set<ConstraintViolation<Events>> violaciones = validator.validate(evento);
        assertFalse(violaciones.isEmpty());
    }

    @Test
    void debeFallarSiFechaPublicacionEsFutura() {
        Events evento = crearEventoValido("Publicacion Futura");
        evento.setFechaPublicacion(LocalDate.now().plusDays(2));

        Set<ConstraintViolation<Events>> violaciones = validator.validate(evento);
        assertFalse(violaciones.isEmpty());
    }

    @Test
    void debeFallarSiFechaEstrenoEsPasada() {
        Events evento = crearEventoValido("Estreno Pasado");
        evento.setFechaEstreno(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<Events>> violaciones = validator.validate(evento);
        assertFalse(violaciones.isEmpty());
    }

    @Test
    void debeValidarQueTeatroNoPermiteFestivos() {
        Events evento = crearEventoValido("Obra de Teatro");
        evento.setTipo(Events.TipoEvento.TEATRO);
        evento.setDiasDisponibles(EnumSet.of(Events.DiasDisponibles.FESTIVO));

        assertThrows(IllegalArgumentException.class, evento::validarReglas);
    }

    @Test
    void debeCalcularDiasFaltantesCorrectamente() {
        Events evento = crearEventoValido("Estreno Película");
        evento.setFechaEstreno(LocalDate.now().plusDays(3));

        String mensaje = evento.diasFaltantes();
        assertTrue(mensaje.contains("Faltan 3 días"));
    }
}