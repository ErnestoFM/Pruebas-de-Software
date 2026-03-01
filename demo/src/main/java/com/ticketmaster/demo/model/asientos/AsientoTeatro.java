package com.ticketmaster.demo.model.asientos;

import com.ticketmaster.demo.model.Events;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class AsientoTeatro extends AsientoBase {

    private boolean comprado;
    private boolean bloqueado;
    private String fila;
    private int numero;
    @Enumerated(EnumType.STRING)
    private TipoAsiento tipoAsiento;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Events evento;

    @Override
    public String getTipoEspecifico () {
        return this.getTipoAsiento().name();
    }

    @Getter
    public enum TipoAsiento {
        PALCO(500.0),
        LUNETA(300.0),
        GENERAL(150.0),
        VIP(800.0);

        private final double precioBase;

        TipoAsiento(double precioBase) {
            this.precioBase = precioBase;
        }

    }

    @PreUpdate
    public void asignarPrecio() {
        if (tipoAsiento != null) {
            this.setPrecio(tipoAsiento .getPrecioBase()); // usar setter
        }
    }
}