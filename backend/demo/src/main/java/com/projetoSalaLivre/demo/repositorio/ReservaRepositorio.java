package com.projetoSalaLivre.demo.repositorio;

import com.projetoSalaLivre.demo.model.Periodo;
import com.projetoSalaLivre.demo.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {

    List<Reserva> findByDataAndPeriodo(
            LocalDate data,
            Periodo periodo
    );

    boolean existsBySalaId(Long salaId);

    boolean existsBySalaIdAndDataAndPeriodo(
            Long salaId,
            LocalDate data,
            Periodo periodo
    );

    Optional<Reserva> findBySalaIdAndDataAndPeriodo(
            Long salaId,
            LocalDate data,
            Periodo periodo
    );
}