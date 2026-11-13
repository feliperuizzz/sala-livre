package com.projetoSalaLivre.demo.controllers;

import com.projetoSalaLivre.demo.dto.ProfessorAtualizacaoRequestDTO;
import com.projetoSalaLivre.demo.dto.ProfessorCadastroRequestDTO;
import com.projetoSalaLivre.demo.dto.ProfessorResponseDTO;
import com.projetoSalaLivre.demo.service.ProfessorService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/professores")
public class AdminProfessorController {

    private final ProfessorService professorService;


    public AdminProfessorController(
            ProfessorService professorService
    ) {

        this.professorService =
                professorService;
    }


    @GetMapping
    public List<ProfessorResponseDTO> listar() {

        return professorService
                .listar();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessorResponseDTO cadastrar(
            @Valid
            @RequestBody
            ProfessorCadastroRequestDTO request
    ) {

        return professorService
                .cadastrar(request);
    }

    @PutMapping("/{id}")
    public ProfessorResponseDTO editar(
            @PathVariable Long id,
            @Valid
            @RequestBody
            ProfessorAtualizacaoRequestDTO request
    ) {

        return professorService
                .editar(id, request);
    }

    @PatchMapping("/{id}/situacao")
    public ProfessorResponseDTO alterarSituacao(
            @PathVariable Long id
    ) {

        return professorService
                .alterarSituacao(id);
    }
}