package com.projetoSalaLivre.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record BlocoRequestDTO(

        @NotBlank(message = "O código do bloco é obrigatório.")
        String codigo

) {
}