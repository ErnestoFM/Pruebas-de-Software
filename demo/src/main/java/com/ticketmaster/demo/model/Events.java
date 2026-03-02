package com.ticketmaster.demo.model;

import com.ticketmaster.demo.model.asientos.AsientoBase;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del evento no puede estar vacío")
    private String nombre;

    @NotNull(message = "Debe de tener un tiempo de duracion")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    private Integer duracion;

    private Boolean activo = true;

    @NotNull
    @PastOrPresent(message = "La fecha de publicacion no puede ser futura")
    private LocalDate fechaPublicacion;

    @NotNull
    @FutureOrPresent(message = "la fecha no puede ser en el pasado")
    private LocalDate fechaEstreno;

    @ElementCollection(targetClass = RopaPermitida.class)
    @Enumerated(EnumType.STRING)
    private Set<RopaPermitida> ropaPermitida = EnumSet.allOf(RopaPermitida.class);

    @NotNull(message = "Debe seleccionar si es Cine, Teatro o Museo")
    @Enumerated(EnumType.STRING)
    private TipoEvento tipo;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    @NotNull
    private int maxBoletosPorUsuario=10;

    @NotNull
    private Boolean abiertoEnFestivos = true;

    @ElementCollection(targetClass = DiasDisponibles.class)
    @Enumerated(EnumType.STRING)
    private Set<DiasDisponibles> diasDisponibles = EnumSet.allOf(DiasDisponibles.class);
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<AsientoBase> asientos;

    @Column(name = "image_id")
    private String imageId;
    public enum TipoEvento {
        CINE,
        TEATRO,
        MUSEO
    }

    public enum DiasDisponibles{
        LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO, FESTIVO
    }

    public void validarReglas() {
        if (tipo == TipoEvento.TEATRO && diasDisponibles.contains(DiasDisponibles.FESTIVO)) {
            throw new IllegalArgumentException("Los eventos de TEATRO no pueden tener días festivos");
        }
    }

    public enum RopaPermitida{
        FORMAL, CASUAL, ELEGANTE
    }

    //Asegurarme de que nunca quede el campo en null
    @PrePersist
    public void prePersist() {
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDate.now();
        }
    }

    @Transactional
    public String diasFaltantes() {
        long dias = ChronoUnit.DAYS.between(fechaPublicacion, fechaEstreno);

        if (dias > 0) {
            return "Faltan " + dias + " días para el estreno";
        } else if (dias == 0) {
            return "¡El estreno es hoy!";
        } else {
            return "El estreno fue hace " + Math.abs(dias) + " días";
        }
    }
}