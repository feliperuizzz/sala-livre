package com.projetoSalaLivre.demo.controllers;

import com.projetoSalaLivre.demo.dto.BlocoRequestDTO;
import com.projetoSalaLivre.demo.dto.BlocoResponseDTO;
import com.projetoSalaLivre.demo.service.BlocoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/blocos")
public class AdminBlocoController {

    private final BlocoService blocoService;

    public AdminBlocoController(
            BlocoService blocoService
    ) {
        this.blocoService = blocoService;
    }


    @GetMapping
    public List<BlocoResponseDTO> listar() {
        return blocoService.listar();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlocoResponseDTO cadastrar(
            @Valid
            @RequestBody
            BlocoRequestDTO request
    ) {
        return blocoService.cadastrar(request);
    }


    @PutMapping("/{id}")
    public BlocoResponseDTO editar(
            @PathVariable Long id,
            @Valid
            @RequestBody
            BlocoRequestDTO request
    ) {
        return blocoService.editar(id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @PathVariable Long id
    ) {
        blocoService.excluir(id);
    }
}