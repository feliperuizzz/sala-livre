package com.projetoSalaLivre.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record SalaAtualizacaoRequestDTO(

        @NotBlank(message = "O código da sala é obrigatório.")
        String codigo

) {
}