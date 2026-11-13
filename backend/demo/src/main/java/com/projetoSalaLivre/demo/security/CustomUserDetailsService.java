package com.projetoSalaLivre.demo.security;

import com.projetoSalaLivre.demo.model.Professor;
import com.projetoSalaLivre.demo.repositorio.ProfessorRepositorio;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ProfessorRepositorio professorRepositorio;

    public CustomUserDetailsService(ProfessorRepositorio professorRepositorio) {
        this.professorRepositorio = professorRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        return professorRepositorio.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Professor não encontrado")
                );
    }

    public Professor buscarProfessorPorEmail(String email) {

        return professorRepositorio.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Professor não encontrado")
                );
    }
}