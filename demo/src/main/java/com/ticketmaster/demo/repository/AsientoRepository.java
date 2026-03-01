package com.ticketmaster.demo.repository;

import com.ticketmaster.demo.model.asientos.AsientoCine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsientoRepository extends JpaRepository<AsientoCine, Long> {
    List<AsientoCine> findByEventoId(Long eventoId);
}
