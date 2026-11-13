package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.dto.BlocoRequestDTO;
import com.projetoSalaLivre.demo.dto.BlocoResponseDTO;
import com.projetoSalaLivre.demo.model.Bloco;
import com.projetoSalaLivre.demo.repositorio.BlocoRepositorio;

import com.projetoSalaLivre.demo.repositorio.SalaRepositorio;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class BlocoService {

    private final BlocoRepositorio blocoRepositorio;
    private final SalaRepositorio salaRepositorio;

    public BlocoService(
            BlocoRepositorio blocoRepositorio,
            SalaRepositorio salaRepositorio
    ) {

        this.blocoRepositorio =
                blocoRepositorio;

        this.salaRepositorio =
                salaRepositorio;
    }


    public List<BlocoResponseDTO> listar() {

        return blocoRepositorio
                .findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                Bloco::getCodigo
                        )
                )
                .map(
                        bloco ->
                                new BlocoResponseDTO(
                                        bloco.getId(),
                                        bloco.getCodigo()
                                )
                )
                .toList();
    }


    public BlocoResponseDTO cadastrar(
            BlocoRequestDTO request
    ) {

        String codigo =
                request.codigo()
                        .trim()
                        .toUpperCase();


        if (
                blocoRepositorio
                        .existsByCodigo(codigo)
        ) {

            throw new IllegalStateException(
                    "Já existe um bloco cadastrado com este código."
            );
        }


        Bloco bloco = new Bloco();

        bloco.setCodigo(codigo);


        bloco =
                blocoRepositorio.save(bloco);


        return new BlocoResponseDTO(
                bloco.getId(),
                bloco.getCodigo()
        );
    }

    public BlocoResponseDTO editar(
            Long id,
            BlocoRequestDTO request
    ) {

        Bloco bloco =
                blocoRepositorio
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Bloco não encontrado."
                                )
                        );

        String codigo =
                request.codigo()
                        .trim()
                        .toUpperCase();

        blocoRepositorio
                .findByCodigo(codigo)
                .ifPresent(outroBloco -> {

                    if (!outroBloco.getId().equals(id)) {

                        throw new IllegalStateException(
                                "Já existe outro bloco cadastrado com este código."
                        );
                    }
                });

        bloco.setCodigo(codigo);

        bloco =
                blocoRepositorio.save(bloco);

        return new BlocoResponseDTO(
                bloco.getId(),
                bloco.getCodigo()
        );
    }

    public void excluir(Long id) {

        Bloco bloco =
                blocoRepositorio
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Bloco não encontrado."
                                )
                        );

        if (
                salaRepositorio
                        .existsByBlocoId(id)
        ) {

            throw new IllegalStateException(
                    "Não é possível excluir este bloco, pois existem salas associadas a ele."
            );
        }

        blocoRepositorio.delete(bloco);
    }
}