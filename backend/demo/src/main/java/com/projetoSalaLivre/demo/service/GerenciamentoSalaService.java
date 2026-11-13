package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.dto.SalaCadastroRequestDTO;
import com.projetoSalaLivre.demo.dto.SalaGerenciamentoDTO;
import com.projetoSalaLivre.demo.model.Bloco;
import com.projetoSalaLivre.demo.model.Sala;
import com.projetoSalaLivre.demo.repositorio.BlocoRepositorio;
import com.projetoSalaLivre.demo.repositorio.SalaRepositorio;
import com.projetoSalaLivre.demo.dto.SalaAtualizacaoRequestDTO;
import com.projetoSalaLivre.demo.repositorio.ReservaRepositorio;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GerenciamentoSalaService {

    private final SalaRepositorio salaRepositorio;
    private final ReservaRepositorio reservaRepositorio;
    private final BlocoRepositorio blocoRepositorio;

    private int compararCodigosSala(
            String codigo1,
            String codigo2
    ) {
        try {
            long numero1 = Long.parseLong(codigo1);
            long numero2 = Long.parseLong(codigo2);

            return Long.compare(numero1, numero2);
        } catch (NumberFormatException e) {
            return codigo1.compareToIgnoreCase(codigo2);
        }
    }

    public GerenciamentoSalaService(
            SalaRepositorio salaRepositorio,
            BlocoRepositorio blocoRepositorio,
            ReservaRepositorio reservaRepositorio
    ) {

        this.salaRepositorio = salaRepositorio;
        this.blocoRepositorio = blocoRepositorio;
        this.reservaRepositorio = reservaRepositorio;
    }


    public List<SalaGerenciamentoDTO> listar() {

        return salaRepositorio
                .findAll()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        (Sala sala) ->
                                                sala
                                                        .getBloco()
                                                        .getCodigo()
                                )
                                .thenComparing(
                                        Sala::getCodigo,
                                        this::compararCodigosSala
                                )
                )
                .map(this::converter)
                .toList();
    }


    public SalaGerenciamentoDTO cadastrar(
            SalaCadastroRequestDTO request
    ) {

        String codigo =
                request.codigo()
                        .trim()
                        .toUpperCase();


        Bloco bloco =
                blocoRepositorio
                        .findById(request.blocoId())
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Bloco não encontrado."
                                )
                        );


        if (
                salaRepositorio
                        .existsByBlocoIdAndCodigo(
                                bloco.getId(),
                                codigo
                        )
        ) {

            throw new IllegalStateException(
                    "Já existe uma sala com este código neste bloco."
            );
        }


        Sala sala = new Sala();

        sala.setCodigo(codigo);
        sala.setBloco(bloco);


        sala =
                salaRepositorio.save(sala);


        return converter(sala);
    }


    private SalaGerenciamentoDTO converter(
            Sala sala
    ) {

        return new SalaGerenciamentoDTO(
                sala.getId(),
                sala.getCodigo(),
                sala.getBloco().getId(),
                sala.getBloco().getCodigo()
        );
    }

    public SalaGerenciamentoDTO editar(
            Long id,
            SalaAtualizacaoRequestDTO request
    ) {

        Sala sala =
                salaRepositorio
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Sala não encontrada."
                                )
                        );


        String codigo =
                request.codigo()
                        .trim()
                        .toUpperCase();


        salaRepositorio
                .findByBlocoIdAndCodigo(
                        sala.getBloco().getId(),
                        codigo
                )
                .ifPresent(outraSala -> {

                    if (!outraSala.getId().equals(id)) {

                        throw new IllegalStateException(
                                "Já existe outra sala com este código neste bloco."
                        );
                    }
                });


        sala.setCodigo(codigo);

        sala =
                salaRepositorio.save(sala);


        return converter(sala);
    }

    public void excluir(Long id) {

        Sala sala =
                salaRepositorio
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Sala não encontrada."
                                )
                        );


        if (
                reservaRepositorio
                        .existsBySalaId(id)
        ) {

            throw new IllegalStateException(
                    "Não é possível excluir esta sala, pois existem reservas associadas a ela."
            );
        }


        salaRepositorio.delete(sala);
    }
}