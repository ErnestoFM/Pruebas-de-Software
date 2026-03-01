package com.ticketmaster.demo.model.asientos;

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
public class AsientoMuseo extends AsientoBase {

    private boolean comprado;
    private String fila;
    private int numero;


    @Override
    public String getTipoEspecifico() {
        return "General";
    }
}