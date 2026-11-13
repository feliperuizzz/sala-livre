package com.projetoSalaLivre.demo.dto;
import com.projetoSalaLivre.demo.model.Perfil;

public record ProfessorResponseDTO(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        boolean ativo
) {
}