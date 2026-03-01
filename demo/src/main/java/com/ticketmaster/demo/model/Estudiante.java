package com.ticketmaster.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Estudiante {
    private String nombre;
    private String codigo;
    private String carrera;
}