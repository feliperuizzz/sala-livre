package com.projetoSalaLivre.demo.repositorio;

import com.projetoSalaLivre.demo.model.Bloco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlocoRepositorio
        extends JpaRepository<Bloco, Long> {

    Optional<Bloco> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}