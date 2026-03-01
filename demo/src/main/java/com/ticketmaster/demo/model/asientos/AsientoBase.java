package com.ticketmaster.demo.model.asientos;

import com.ticketmaster.demo.model.Events;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public abstract class AsientoBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double precio;

    @NotNull(message = "Debe de tener una fecha de compra")
    @PastOrPresent(message = "La fecha de compra no puede ser futura")
    private LocalDate fechaCompra;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "evento_id") // Esto crea la columna en la base de datos
    private Events evento;

    @PrePersist
    public void inicializarFechaCompra() {
        if (fechaCompra == null) {
            fechaCompra = LocalDate.now();
        }
    }

    public abstract String getTipoEspecifico();
}