package com.projetoSalaLivre.demo.repositorio;

import com.projetoSalaLivre.demo.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaRepositorio
        extends JpaRepository<Sala, Long> {

    boolean existsByBlocoId(Long blocoId);

    boolean existsByBlocoIdAndCodigo(
            Long blocoId,
            String codigo
    );

    Optional<Sala> findByBlocoIdAndCodigo(
            Long blocoId,
            String codigo
    );
}