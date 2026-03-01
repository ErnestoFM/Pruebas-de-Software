package com.ticketmaster.demo.controller;

import com.ticketmaster.demo.model.Events;
import com.ticketmaster.demo.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;

@RestController
@RequestMapping("/api/eventos")
public class EventController {

    private final EventoService service;

    public EventController(EventoService service) {
        this.service = service;
    }

    @PostMapping("/{id}/comprar/museo")
    public ResponseEntity<Events> comprarBoletoMuseo(@PathVariable Long id, @RequestParam int cantidad, @RequestParam LocalDate fechaCompra ) {
        try {
            Events evento = service.comprarBoletoMuseo(id, cantidad, fechaCompra);
            return ResponseEntity.ok(evento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 400 Si no hay stock
        }
    }

    @PostMapping("/{id}/comprar/cine")
    public ResponseEntity<Events> comprarBoletoCine(@PathVariable Long id,
                                                    @RequestParam int cantidad,
                                                    @RequestParam LocalDate fechaCompra) {
        try {
            Events evento = service.comprarBoletoCine(id, cantidad, fechaCompra);
            return ResponseEntity.ok(evento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 400 si no hay stock o reglas inválidas
        }
    }

    @PostMapping("/{id}/comprar/teatro")
    public ResponseEntity<Events> comprarBoletoTeatro(@PathVariable Long id,
                                                      @RequestParam int cantidad,
                                                      @RequestParam LocalDate fechaCompra) {
        try {
            Events evento = service.comprarBoletoTeatro(id, cantidad, fechaCompra);
            return ResponseEntity.ok(evento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 400 si no hay stock o reglas inválidas
        }
    }
}
