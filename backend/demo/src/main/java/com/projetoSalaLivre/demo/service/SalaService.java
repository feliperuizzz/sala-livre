package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.dto.SalaDisponibilidadeDTO;
import com.projetoSalaLivre.demo.model.Periodo;
import com.projetoSalaLivre.demo.model.Reserva;
import com.projetoSalaLivre.demo.model.Sala;
import com.projetoSalaLivre.demo.repositorio.ReservaRepositorio;
import com.projetoSalaLivre.demo.repositorio.SalaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SalaService {

    private int compararCodigosSala(
            String codigo1,
            String codigo2
    ) {
        try {
            long numero1 = Long.parseLong(codigo1);
            long numero2 = Long.parseLong(codigo2);

            return Long.compare(numero1, numero2);
        } catch (NumberFormatException e) {
            return codigo1.compareToIgnoreCase(codigo2);
        }
    }

    private final SalaRepositorio salaRepositorio;
    private final ReservaRepositorio reservaRepositorio;

    public SalaService(
            SalaRepositorio salaRepositorio,
            ReservaRepositorio reservaRepositorio
    ) {
        this.salaRepositorio = salaRepositorio;
        this.reservaRepositorio = reservaRepositorio;
    }

    public List<Sala> listarTodas() {

        return salaRepositorio.findAll()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        (Sala sala) ->
                                                sala.getBloco().getCodigo()
                                )
                                .thenComparing(
                                        Sala::getCodigo,
                                        this::compararCodigosSala
                                )
                )
                .toList();
    }

    public Sala buscarPorId(Long id) {

        return salaRepositorio.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Sala não encontrada.")
                );
    }

    @Transactional(readOnly = true)
    public List<SalaDisponibilidadeDTO> consultarDisponibilidade(
            LocalDate data,
            Periodo periodo
    ) {

        if (data == null) {
            throw new RuntimeException("A data deve ser informada.");
        }

        if (periodo == null) {
            throw new RuntimeException("O período deve ser informado.");
        }

        List<Sala> salas = listarTodas();

        return salas.stream()
                .map(sala -> montarDisponibilidade(
                        sala,
                        data,
                        periodo
                ))
                .toList();
    }

    private SalaDisponibilidadeDTO montarDisponibilidade(
            Sala sala,
            LocalDate data,
            Periodo periodo
    ) {

        Optional<Reserva> reserva =
                reservaRepositorio.findBySalaIdAndDataAndPeriodo(
                        sala.getId(),
                        data,
                        periodo
                );

        String codigoVisual =
                sala.getBloco().getCodigo()
                        + sala.getCodigo();

        if (reserva.isEmpty()) {

            return new SalaDisponibilidadeDTO(
                    sala.getId(),
                    codigoVisual,
                    sala.getBloco().getCodigo(),
                    true,
                    null,
                    null,
                    null
            );
        }


        Reserva reservaEncontrada = reserva.get();

        return new SalaDisponibilidadeDTO(
                sala.getId(),
                codigoVisual,
                sala.getBloco().getCodigo(),
                false,
                reservaEncontrada.getId(),
                reservaEncontrada.getProfessor().getId(),
                reservaEncontrada.getProfessor().getNome()
        );
    }
}