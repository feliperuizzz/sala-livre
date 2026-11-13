package com.projetoSalaLivre.demo.dto;

import com.projetoSalaLivre.demo.model.Periodo;

import java.time.LocalDate;

public record ReservaRequestDTO(
        Long salaId,
        LocalDate data,
        Periodo periodo
) {
}