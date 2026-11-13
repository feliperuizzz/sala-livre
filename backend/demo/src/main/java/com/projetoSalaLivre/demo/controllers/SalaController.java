package com.projetoSalaLivre.demo.controllers;

import com.projetoSalaLivre.demo.dto.SalaDisponibilidadeDTO;
import com.projetoSalaLivre.demo.model.Periodo;
import com.projetoSalaLivre.demo.service.SalaService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping("/disponibilidade")
    public List<SalaDisponibilidadeDTO> consultarDisponibilidade(
            @RequestParam LocalDate data,
            @RequestParam Periodo periodo
    ) {

        return salaService.consultarDisponibilidade(
                data,
                periodo
        );
    }
}