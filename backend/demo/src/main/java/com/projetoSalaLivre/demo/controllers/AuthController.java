package com.projetoSalaLivre.demo.controllers;

import com.projetoSalaLivre.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.projetoSalaLivre.demo.dto.ProfessorResponseDTO;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String senha = body.get("senha");

        String token = authService.login(email, senha);

        return Map.of("token", token);
    }

    @GetMapping("/me")
    public ProfessorResponseDTO professorAtual() {

        return authService.obterProfessorAtual();
    }
}