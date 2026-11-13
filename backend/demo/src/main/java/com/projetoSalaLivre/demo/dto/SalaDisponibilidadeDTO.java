package com.projetoSalaLivre.demo.dto;

public record SalaDisponibilidadeDTO(
        Long salaId,
        String codigo,
        String bloco,
        boolean disponivel,
        Long reservaId,
        Long professorId,
        String professor
) {
}