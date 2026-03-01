package com.ticketmaster.demo;

import com.ticketmaster.demo.exception.StockInsuficienteException;
import com.ticketmaster.demo.model.Events;
import com.ticketmaster.demo.repository.EventoRepository;
import com.ticketmaster.demo.service.EventoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Disabled
class EventoServiceTest {

    @Mock
    private EventoRepository repository;

    @InjectMocks
    private EventoService service;

    private Events crearEventoValido() {
        return new Events(
                1L,
                "Evento Demo",
                120, // duracion
                true, // activo
                LocalDate.now(), // fechaPublicacion
                LocalDate.now().plusDays(5), // fechaEstreno
                EnumSet.of(Events.RopaPermitida.CASUAL), // ropaPermitida
                Events.TipoEvento.MUSEO, // tipo
                100, // stock
                10, // maxBoletosPorUsuario
                true, // abiertoEnFestivos
                Set.of(Events.DiasDisponibles.LUNES, Events.DiasDisponibles.MARTES), // diasDisponibles
                null
        );
    }

    @Test
    void debeRestarStockAlComprarBoleto() {
        Events eventoMock = crearEventoValido();

        when(repository.findById(1L)).thenReturn(Optional.of(eventoMock));
        when(repository.save(any(Events.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Events resultado = service.comprarBoletoMuseo(1L, 10 , LocalDate.now());

        assertEquals(90, resultado.getStock());
        verify(repository).save(eventoMock);
    }

    @Test
    void debeLanzarExcepcionSiNoHayStock() {
        Events eventoMock = crearEventoValido();
        eventoMock.setStock(5);

        when(repository.findById(1L)).thenReturn(Optional.of(eventoMock));

        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                () -> service.comprarBoletoMuseo(1L, 10, LocalDate.now())
        );

        assertEquals("No hay suficientes boletos disponibles. Quedan: 5", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void debeCalcularDiasFaltantesCorrectamente() {
        Events evento = crearEventoValido();
        evento.setFechaEstreno(LocalDate.now().plusDays(3));

        String mensaje = evento.diasFaltantes();
        assertTrue(mensaje.contains("Faltan 3 días"));
    }

    @Test
    void debeValidarReglasDeTeatroSinFestivos() {
        Events evento = crearEventoValido();
        evento.setTipo(Events.TipoEvento.TEATRO);
        evento.setDiasDisponibles(Set.of(Events.DiasDisponibles.FESTIVO));

        assertThrows(IllegalArgumentException.class, evento::validarReglas);
    }

    @Test
    void debeFallarSiDuracionEsMenorQueUno() {
        Events evento = crearEventoValido();
        evento.setDuracion(0);

        Set<ConstraintViolation<Events>> violaciones = Validation.buildDefaultValidatorFactory()
                .getValidator().validate(evento);

        assertFalse(violaciones.isEmpty());
    }

    @Test
    void debeFallarSiFechaPublicacionEsFutura() {
        Events evento = crearEventoValido();
        evento.setFechaPublicacion(LocalDate.now().plusDays(2));

        Set<ConstraintViolation<Events>> violaciones = Validation.buildDefaultValidatorFactory()
                .getValidator().validate(evento);

        assertFalse(violaciones.isEmpty());
    }

    @Test
    void debeFallarSiFechaEstrenoEsPasada() {
        Events evento = crearEventoValido();
        evento.setFechaEstreno(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<Events>> violaciones = Validation.buildDefaultValidatorFactory()
                .getValidator().validate(evento);

        assertFalse(violaciones.isEmpty());
    }

    @Test
    void debePermitirCompraEnFestivoSiMuseoAbierto() {
        Events evento = crearEventoValido();
        evento.setTipo(Events.TipoEvento.MUSEO);
        evento.setAbiertoEnFestivos(true);
        evento.setStock(20);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));
        when(repository.save(any(Events.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Events resultado = service.comprarBoletoMuseo(1L, 5, LocalDate.now().with(DayOfWeek.SUNDAY));

        assertEquals(15, resultado.getStock());
    }

    @Test
    void debeLanzarExcepcionSiMuseoCerradoEnFestivo() {
        Events evento = crearEventoValido();
        evento.setTipo(Events.TipoEvento.MUSEO);
        evento.setAbiertoEnFestivos(false);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(IllegalArgumentException.class, () -> service.comprarBoletoMuseo(1L, 5, LocalDate.now().with(DayOfWeek.SUNDAY)));
    }

    @Test
    void debeLanzarExcepcionSiUsuarioCompraMasDeMaximoPermitido() {
        Events eventoMock = crearEventoValido();
        eventoMock.setMaxBoletosPorUsuario(10);
        eventoMock.setStock(50);

        when(repository.findById(1L)).thenReturn(Optional.of(eventoMock));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> service.comprarBoletoMuseo(1L, 15, LocalDate.now()));

        assertEquals("No puedes comprar más boletos que el máximo permitido", exception.getMessage());
    }
}