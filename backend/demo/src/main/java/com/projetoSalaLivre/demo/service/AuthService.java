package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.dto.ProfessorResponseDTO;
import com.projetoSalaLivre.demo.model.Professor;
import com.projetoSalaLivre.demo.repositorio.ProfessorRepositorio;
import com.projetoSalaLivre.demo.security.JwtService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final ProfessorRepositorio professorRepositorio;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            ProfessorRepositorio professorRepositorio,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.professorRepositorio = professorRepositorio;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email, String senha) {

        Professor professor = professorRepositorio
                .findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "E-mail ou senha inválidos."
                        )
                );

        if (!passwordEncoder.matches(
                senha,
                professor.getSenha()
        )) {
            throw new BadCredentialsException(
                    "E-mail ou senha inválidos."
            );
        }

        if (!professor.isAtivo()) {
            throw new BadCredentialsException(
                    "Este usuário está desativado."
            );
        }

        return jwtService.gerarToken(
                professor.getEmail()
        );
    }

    public ProfessorResponseDTO obterProfessorAtual() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null ||
                        !authentication.isAuthenticated() ||
                        "anonymousUser".equals(
                                authentication.getPrincipal()
                        )
        ) {
            throw new AccessDeniedException(
                    "Professor não autenticado."
            );
        }

        String email = authentication.getName();

        Professor professor =
                professorRepositorio
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Professor autenticado não encontrado."
                                )
                        );

        return new ProfessorResponseDTO(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getPerfil(),
                professor.isAtivo()
        );
    }
}