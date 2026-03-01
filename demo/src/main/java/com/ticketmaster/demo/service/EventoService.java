package com.ticketmaster.demo.service;

import com.ticketmaster.demo.exception.StockInsuficienteException;
import com.ticketmaster.demo.model.asientos.AsientoCine;
import com.ticketmaster.demo.model.Events;
import com.ticketmaster.demo.repository.AsientoRepository;
import com.ticketmaster.demo.repository.EventoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final AsientoRepository asientoRepository;

    // lista de festivos predefinidos
    private final Set<LocalDate> festivos = Set.of(
            LocalDate.of(2026, 1, 1),   // Año Nuevo
            LocalDate.of(2026, 2, 5),   // Constitución
            LocalDate.of(2026, 12, 25)  // Navidad
    );


    public EventoService(EventoRepository eventoRepository, AsientoRepository asientoRepository) {
        this.eventoRepository = eventoRepository;
        this.asientoRepository = asientoRepository;
    }

    public AsientoCine reservarAsiento(Long asientoId) {
        AsientoCine asiento = asientoRepository.findById(asientoId)
                .orElseThrow(() -> new IllegalArgumentException("El asiento no existe"));

        if (asiento.isComprado()) {
            throw new IllegalStateException("Lo sentimos, el asiento " + asiento.getFila() + "-" + asiento.getNumero() + " ya fue comprado por otro usuario.");
        }

        asiento.setBloqueado(true);


        return asientoRepository.save(asiento);
    }

    @Transactional
    public Events comprarBoletoMuseo(Long idEvento, int cantidad, LocalDate fechaCompra) {

        Events evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado"));

        if (evento.getTipo() != Events.TipoEvento.MUSEO) {
            throw new IllegalArgumentException("Este método solo aplica a eventos de tipo MUSEO");
        }

        boolean esFestivo = festivos.contains(fechaCompra) || fechaCompra.getDayOfWeek() == DayOfWeek.SUNDAY;
        if (esFestivo && !Boolean.TRUE.equals(evento.getAbiertoEnFestivos())) {
            throw new IllegalArgumentException("El Museo está cerrado por día festivo.");
        }

        // Validar stock
        validarStock(evento.getStock(), cantidad);

        //Validar numero MAX de boletos por usuario
        if (cantidad > evento.getMaxBoletosPorUsuario()) {
            throw new IllegalArgumentException("No puedes comprar más boletos que el máximo permitido");
        }

        evento.setStock(evento.getStock() - cantidad);

        return eventoRepository.save(evento);
    }

    @Transactional
    public Events comprarBoletoCine(Long idEvento, int cantidad, LocalDate fechaCompra) {

        Events evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado"));

        if (evento.getTipo() != Events.TipoEvento.CINE) {
            throw new IllegalArgumentException("Este método solo aplica a eventos de tipo CINE");
        }

        // Validar stock
        validarStock(evento.getStock(), cantidad);

        // Validar número máximo de boletos por usuario
        if (cantidad > evento.getMaxBoletosPorUsuario()) {
            throw new IllegalArgumentException("No puedes comprar más boletos que el máximo permitido");
        }

        evento.setStock(evento.getStock() - cantidad);

        return eventoRepository.save(evento);
    }

    @Transactional
    public Events comprarBoletoTeatro(Long idEvento, int cantidad, LocalDate fechaCompra) {

        Events evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado"));

        if (evento.getTipo() != Events.TipoEvento.TEATRO) {
            throw new IllegalArgumentException("Este método solo aplica a eventos de tipo TEATRO");
        }

        boolean esFestivo = festivos.contains(fechaCompra) || fechaCompra.getDayOfWeek() == DayOfWeek.SUNDAY;
        if (esFestivo) {
            throw new IllegalArgumentException("Los eventos de TEATRO no pueden realizarse en días festivos");
        }

        // Validar stock
        validarStock(evento.getStock(), cantidad);

        // Validar número máximo de boletos por usuario
        if (cantidad > evento.getMaxBoletosPorUsuario()) {
            throw new IllegalArgumentException("No puedes comprar más boletos que el máximo permitido");
        }

        evento.setStock(evento.getStock() - cantidad);

        return eventoRepository.save(evento);
    }

    private void validarStock(int stock, int cantidadBoletos){
        if (stock < cantidadBoletos)
            throw new StockInsuficienteException(stock);
    }
}