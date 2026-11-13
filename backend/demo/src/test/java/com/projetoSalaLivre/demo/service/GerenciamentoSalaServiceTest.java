
package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.model.Sala;
import com.projetoSalaLivre.demo.repositorio.BlocoRepositorio;
import com.projetoSalaLivre.demo.repositorio.ReservaRepositorio;
import com.projetoSalaLivre.demo.repositorio.SalaRepositorio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GerenciamentoSalaServiceTest {

    @Mock
    private SalaRepositorio salaRepositorio;

    @Mock
    private BlocoRepositorio blocoRepositorio;

    @Mock
    private ReservaRepositorio reservaRepositorio;

    @InjectMocks
    private GerenciamentoSalaService gerenciamentoSalaService;

    @Test
    void t12_deveImpedirExclusaoDeSalaComReservasAssociadas() {

        Long idSala = 15L;

        Sala sala = new Sala();
        sala.setId(idSala);
        sala.setCodigo("101");

        when(salaRepositorio.findById(idSala))
                .thenReturn(Optional.of(sala));

        when(reservaRepositorio.existsBySalaId(idSala))
                .thenReturn(true);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> gerenciamentoSalaService.excluir(idSala)
        );

        assertEquals(
                "Não é possível excluir esta sala, pois existem reservas associadas a ela.",
                excecao.getMessage()
        );

        verify(salaRepositorio).findById(idSala);

        verify(reservaRepositorio).existsBySalaId(idSala);

        verify(salaRepositorio, never())
                .delete(any(Sala.class));
    }
}
