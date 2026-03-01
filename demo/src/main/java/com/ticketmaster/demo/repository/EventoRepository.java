package com.ticketmaster.demo.repository;

import com.ticketmaster.demo.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Events, Long> {

    Events findByNombre(String nombre);
}