package com.ticketmaster.demo.dto;

import com.ticketmaster.demo.model.Events;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class EventRequest {

    @NotBlank(message = "El nombre del evento no puede estar vacío")
    private String nombre;

    @NotNull(message = "Debe de tener un tiempo de duración")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    private Integer duracion;

    private Boolean activo = true;

    @NotNull
    @PastOrPresent(message = "La fecha de publicación no puede ser futura")
    private LocalDate fechaPublicacion;

    @NotNull
    @FutureOrPresent(message = "La fecha no puede ser en el pasado")
    private LocalDate fechaEstreno;

    @NotNull(message = "Debe seleccionar si es Cine, Teatro o Museo")
    private Events.TipoEvento tipo;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    @NotNull
    private int maxBoletosPorUsuario = 10;

    @NotNull
    private Boolean abiertoEnFestivos = true;

    private Set<Events.DiasDisponibles> diasDisponibles;

    private Set<Events.RopaPermitida> ropaPermitida;
}