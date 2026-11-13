package com.projetoSalaLivre.demo.dto;

import com.projetoSalaLivre.demo.model.Periodo;

import java.time.LocalDate;

public record ReservaResponseDTO(
        Long id,
        Long salaId,
        String salaCodigo,
        String bloco,
        LocalDate data,
        Periodo periodo,
        Long professorId,
        String professorNome
) {
}