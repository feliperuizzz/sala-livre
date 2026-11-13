package com.projetoSalaLivre.demo.controllers;

import com.projetoSalaLivre.demo.dto.ReservaRequestDTO;
import com.projetoSalaLivre.demo.dto.ReservaResponseDTO;
import com.projetoSalaLivre.demo.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criarReserva(
            @RequestBody ReservaRequestDTO request
    ) {

        ReservaResponseDTO reserva =
                reservaService.criarReserva(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reserva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(
            @PathVariable Long id
    ) {

        reservaService.cancelarReserva(id);

        return ResponseEntity.noContent().build();
    }
}