package com.projetoSalaLivre.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalaCadastroRequestDTO(

        @NotBlank(message = "O código da sala é obrigatório.")
        String codigo,

        @NotNull(message = "O bloco é obrigatório.")
        Long blocoId

) {
}