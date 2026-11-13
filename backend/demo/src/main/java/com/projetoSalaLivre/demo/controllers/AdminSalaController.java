package com.projetoSalaLivre.demo.controllers;

import com.projetoSalaLivre.demo.dto.SalaCadastroRequestDTO;
import com.projetoSalaLivre.demo.dto.SalaGerenciamentoDTO;
import com.projetoSalaLivre.demo.service.GerenciamentoSalaService;
import com.projetoSalaLivre.demo.dto.SalaAtualizacaoRequestDTO;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/salas")
public class AdminSalaController {

    private final GerenciamentoSalaService gerenciamentoSalaService;


    public AdminSalaController(
            GerenciamentoSalaService gerenciamentoSalaService
    ) {

        this.gerenciamentoSalaService =
                gerenciamentoSalaService;
    }


    @GetMapping
    public List<SalaGerenciamentoDTO> listar() {

        return gerenciamentoSalaService
                .listar();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalaGerenciamentoDTO cadastrar(
            @Valid
            @RequestBody
            SalaCadastroRequestDTO request
    ) {

        return gerenciamentoSalaService
                .cadastrar(request);
    }

    @PutMapping("/{id}")
    public SalaGerenciamentoDTO editar(
            @PathVariable Long id,
            @Valid
            @RequestBody
            SalaAtualizacaoRequestDTO request
    ) {

        return gerenciamentoSalaService
                .editar(id, request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @PathVariable Long id
    ) {

        gerenciamentoSalaService
                .excluir(id);
    }

}