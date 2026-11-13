package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.dto.ProfessorAtualizacaoRequestDTO;
import com.projetoSalaLivre.demo.dto.ProfessorCadastroRequestDTO;
import com.projetoSalaLivre.demo.dto.ProfessorResponseDTO;
import com.projetoSalaLivre.demo.model.Perfil;
import com.projetoSalaLivre.demo.model.Professor;
import com.projetoSalaLivre.demo.repositorio.ProfessorRepositorio;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepositorio professorRepositorio;

    private final PasswordEncoder passwordEncoder;


    public ProfessorService(
            ProfessorRepositorio professorRepositorio,
            PasswordEncoder passwordEncoder
    ) {

        this.professorRepositorio =
                professorRepositorio;

        this.passwordEncoder =
                passwordEncoder;
    }


    public List<ProfessorResponseDTO> listar() {

        return professorRepositorio
                .findAll()
                .stream()
                .filter(
                        professor ->
                                professor.getPerfil()
                                        == Perfil.PROFESSOR
                )
                .sorted(
                        Comparator.comparing(
                                Professor::getNome
                        )
                )
                .map(this::converter)
                .toList();
    }


    public ProfessorResponseDTO cadastrar(
            ProfessorCadastroRequestDTO request
    ) {

        String email =
                request.email()
                        .trim()
                        .toLowerCase();


        if (
                professorRepositorio
                        .existsByEmail(email)
        ) {

            throw new IllegalStateException(
                    "Já existe um Professor cadastrado com este e-mail."
            );
        }


        Professor professor =
                new Professor();

        professor.setNome(
                request.nome().trim()
        );

        professor.setEmail(email);

        professor.setSenha(
                passwordEncoder.encode(
                        request.senha()
                )
        );

        professor.setPerfil(
                Perfil.PROFESSOR
        );
        professor.setAtivo(true);


        professor =
                professorRepositorio
                        .save(professor);


        return converter(professor);
    }

    private ProfessorResponseDTO converter(
            Professor professor
    ) {

        return new ProfessorResponseDTO(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getPerfil(),
                professor.isAtivo()
        );
    }

    public ProfessorResponseDTO editar(
            Long id,
            ProfessorAtualizacaoRequestDTO request
    ) {

        Professor professor =
                professorRepositorio
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Professor não encontrado."
                                )
                        );

        if (professor.getPerfil() != Perfil.PROFESSOR) {

            throw new IllegalArgumentException(
                    "Este usuário não pode ser editado por esta funcionalidade."
            );
        }

        String email =
                request.email()
                        .trim()
                        .toLowerCase();

        professorRepositorio
                .findByEmail(email)
                .ifPresent(outroProfessor -> {

                    if (!outroProfessor.getId().equals(id)) {

                        throw new IllegalStateException(
                                "Já existe um Professor cadastrado com este e-mail."
                        );
                    }
                });

        professor.setNome(
                request.nome().trim()
        );

        professor.setEmail(email);

        professor =
                professorRepositorio.save(professor);

        return converter(professor);
    }
    public ProfessorResponseDTO alterarSituacao(
            Long id
    ) {

        Professor professor =
                professorRepositorio
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Professor não encontrado."
                                )
                        );

        if (professor.getPerfil() != Perfil.PROFESSOR) {

            throw new IllegalArgumentException(
                    "A situação deste usuário não pode ser alterada."
            );
        }

        professor.setAtivo(
                !professor.isAtivo()
        );

        professor =
                professorRepositorio.save(professor);
        return converter(professor);
    }
}