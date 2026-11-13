
package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.model.Perfil;
import com.projetoSalaLivre.demo.model.Professor;
import com.projetoSalaLivre.demo.repositorio.ProfessorRepositorio;
import com.projetoSalaLivre.demo.security.JwtService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.BadCredentialsException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ProfessorRepositorio professorRepositorio;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void t07_deveAutenticarProfessorAtivoComCredenciaisValidas() {

        String email = "professor.teste@exemplo.com";
        String senhaDigitada = "senha-de-teste";
        String senhaArmazenada = "hash-bcrypt-simulado";
        String tokenSimulado = "jwt-simulado-t07";

        Professor professor = new Professor();
        professor.setId(7L);
        professor.setNome("Professor Teste");
        professor.setEmail(email);
        professor.setSenha(senhaArmazenada);
        professor.setPerfil(Perfil.PROFESSOR);
        professor.setAtivo(true);

        when(professorRepositorio.findByEmail(email))
                .thenReturn(Optional.of(professor));

        when(passwordEncoder.matches(senhaDigitada, senhaArmazenada))
                .thenReturn(true);

        when(jwtService.gerarToken(email))
                .thenReturn(tokenSimulado);

        String tokenRetornado =
                authService.login(email, senhaDigitada);

        assertEquals(tokenSimulado, tokenRetornado);

        verify(professorRepositorio).findByEmail(email);

        verify(passwordEncoder)
                .matches(senhaDigitada, senhaArmazenada);

        verify(jwtService).gerarToken(email);
    }


    @Test
    void t08_deveImpedirLoginDeProfessorInativo() {

        String email = "professor.inativo@exemplo.com";
        String senhaDigitada = "senha-correta";
        String senhaArmazenada = "hash-bcrypt-simulado";

        Professor professor = new Professor();
        professor.setId(8L);
        professor.setNome("Professor Inativo");
        professor.setEmail(email);
        professor.setSenha(senhaArmazenada);
        professor.setPerfil(Perfil.PROFESSOR);
        professor.setAtivo(false);

        when(professorRepositorio.findByEmail(email))
                .thenReturn(Optional.of(professor));

        when(passwordEncoder.matches(
                senhaDigitada, senhaArmazenada
        )).thenReturn(true);

        BadCredentialsException excecao = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(email, senhaDigitada)
        );

        assertEquals(
                "Este usuário está desativado.",
                excecao.getMessage()
        );

        verify(professorRepositorio).findByEmail(email);

        verify(passwordEncoder).matches(
                senhaDigitada, senhaArmazenada
        );

        verifyNoInteractions(jwtService);
    }

}
