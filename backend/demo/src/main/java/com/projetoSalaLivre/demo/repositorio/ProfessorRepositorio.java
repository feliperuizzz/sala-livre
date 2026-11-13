package com.projetoSalaLivre.demo.repositorio;

import com.projetoSalaLivre.demo.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepositorio extends JpaRepository<Professor, Long> {

    Optional<Professor> findByEmail(String email);
    boolean existsByEmail(String email);
}