package com.ticketmaster.demo;

import com.ticketmaster.demo.controller.EventController;
import com.ticketmaster.demo.model.Events;
import com.ticketmaster.demo.repository.EventoRepository;
import com.ticketmaster.demo.security.SecurityConfiguration;
import com.ticketmaster.demo.security.SecurityFilter;
import com.ticketmaster.demo.service.EventoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = EventController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}, // 1. Adiós seguridad automática
        excludeFilters = {
                // 2. Adiós a TU configuración de seguridad
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
                // 3. ¡AQUÍ ESTÁ LA CLAVE! Adiós a TU filtro real (que es el que pide la Base de Datos)
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EventoRepository eventoRepository;
    @MockitoBean
    private EventoService eventoService;


    @Test
    void comprarBoletoDebeRetornar200() throws Exception {
        Events eventoMock = new Events();
        eventoMock.setId(1L);
        eventoMock.setNombre("Museo de Arte");
        eventoMock.setStock(95);

        // Simulamos la respuesta del servicio
        when(eventoService.comprarBoletoMuseo(eq(1L), eq(5), any(LocalDate.class)))
                .thenReturn(eventoMock);

        mockMvc.perform(post("/api/eventos/1/comprar/museo")
                        .param("cantidad", "5")
                        .param("fechaCompra", LocalDate.now().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void comprarBoletoDebeRetornarEventoEnJson() throws Exception {
        Events eventoMock = new Events(
                1L,
                "Museo de Arte",
                120,
                true,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                EnumSet.of(Events.RopaPermitida.CASUAL),
                Events.TipoEvento.MUSEO,
                95,
                5,
                true,
                Set.of(Events.DiasDisponibles.LUNES),
                null
        );

        when(eventoService.comprarBoletoMuseo(eq(1L), eq(5), any(LocalDate.class)))
                .thenReturn(eventoMock);

        mockMvc.perform(post("/api/eventos/1/comprar/museo")
                        .param("cantidad", "5")
                        .param("fechaCompra", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Museo de Arte"))
                .andExpect(jsonPath("$.stock").value(95))
                .andExpect(jsonPath("$.tipo").value("MUSEO"));
    }
}