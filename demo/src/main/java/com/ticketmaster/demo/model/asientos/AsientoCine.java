package com.ticketmaster.demo.model.asientos;

import com.ticketmaster.demo.model.Events;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AsientoCine extends AsientoBase {

    private String fila;
    private int numero;
    private boolean comprado;
    private boolean bloqueado;

    @Enumerated(EnumType.STRING)
    private TipoServicio tipoServicio;


    @Override
    public String getTipoEspecifico() {
        return this.getTipoServicio().name();
    }

    @Getter
    public enum TipoServicio {
        CUATRO_DX(350.0),
        IMAX(400.0),
        VR(500.0),
        SCREEN_X(300.0);

        private final double precioBase;

        TipoServicio(double precioBase) {
            this.precioBase = precioBase;
        }

    }

    @PreUpdate
    public void asignarPrecio() {
        if (tipoServicio != null) {
            this.setPrecio(tipoServicio.getPrecioBase()); // usar setter
        }
    }
}