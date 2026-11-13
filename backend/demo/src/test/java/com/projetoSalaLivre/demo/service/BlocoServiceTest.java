
package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.model.Bloco;
import com.projetoSalaLivre.demo.repositorio.BlocoRepositorio;
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
class BlocoServiceTest {

    @Mock
    private BlocoRepositorio blocoRepositorio;

    @Mock
    private SalaRepositorio salaRepositorio;

    @InjectMocks
    private BlocoService blocoService;

    @Test
    void t11_deveImpedirExclusaoDeBlocoComSalasAssociadas() {

        Long idBloco = 10L;

        Bloco bloco = new Bloco();
        bloco.setId(idBloco);
        bloco.setCodigo("B");

        when(blocoRepositorio.findById(idBloco))
                .thenReturn(Optional.of(bloco));

        when(salaRepositorio.existsByBlocoId(idBloco))
                .thenReturn(true);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> blocoService.excluir(idBloco)
        );

        assertEquals(
                "Não é possível excluir este bloco, pois existem salas associadas a ele.",
                excecao.getMessage()
        );

        verify(blocoRepositorio).findById(idBloco);
        verify(salaRepositorio).existsByBlocoId(idBloco);

        verify(blocoRepositorio, never())
                .delete(any(Bloco.class));
    }
}
