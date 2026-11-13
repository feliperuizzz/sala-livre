
package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.dto.ProfessorCadastroRequestDTO;
import com.projetoSalaLivre.demo.dto.ProfessorResponseDTO;
import com.projetoSalaLivre.demo.model.Perfil;
import com.projetoSalaLivre.demo.model.Professor;
import com.projetoSalaLivre.demo.repositorio.ProfessorRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepositorio professorRepositorio;

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    private ProfessorService professorService;

    @BeforeEach
    void preparar() {
        professorService = new ProfessorService(
                professorRepositorio, passwordEncoder
        );
    }

    @Test
    void t09_deveCadastrarProfessorAtivoComPerfilProfessorESenhaBCrypt() {

        String senhaOriginal = "senhaSegura123";

        ProfessorCadastroRequestDTO requisicao =
                new ProfessorCadastroRequestDTO(
                        " Professor Teste ",
                        " Professor.Teste@Exemplo.com ",
                        senhaOriginal
                );

        when(professorRepositorio.save(any(Professor.class)))
                .thenAnswer(invocacao -> {
                    Professor professor = invocacao.getArgument(0);
                    professor.setId(11L);
                    return professor;
                });

        ProfessorResponseDTO resposta =
                professorService.cadastrar(requisicao);

        ArgumentCaptor<Professor> captor =
                ArgumentCaptor.forClass(Professor.class);

        verify(professorRepositorio)
                .existsByEmail("professor.teste@exemplo.com");

        verify(professorRepositorio).save(captor.capture());

        Professor professorSalvo = captor.getValue();

        assertEquals("Professor Teste", professorSalvo.getNome());
        assertEquals(
                "professor.teste@exemplo.com",
                professorSalvo.getEmail()
        );

        assertEquals(Perfil.PROFESSOR, professorSalvo.getPerfil());
        assertTrue(professorSalvo.isAtivo());

        assertNotEquals(senhaOriginal, professorSalvo.getSenha());

        assertTrue(professorSalvo.getSenha().startsWith("$2"));

        assertTrue(passwordEncoder.matches(
                senhaOriginal,
                professorSalvo.getSenha()
        ));

        assertEquals(11L, resposta.id());
        assertEquals("Professor Teste", resposta.nome());
        assertEquals(
                "professor.teste@exemplo.com",
                resposta.email()
        );
        assertEquals(Perfil.PROFESSOR, resposta.perfil());
        assertTrue(resposta.ativo());
    }


    @Test
    void t10_deveImpedirCadastroDeProfessorComEmailDuplicado() {

        String emailExistente = "professor.teste@exemplo.com";

        ProfessorCadastroRequestDTO requisicao =
                new ProfessorCadastroRequestDTO(
                        "Outro Professor",
                        " Professor.Teste@Exemplo.com ",
                        "senhaSegura123"
                );

        when(professorRepositorio.existsByEmail(emailExistente))
                .thenReturn(true);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> professorService.cadastrar(requisicao)
        );

        assertEquals(
                "Já existe um Professor cadastrado com este e-mail.",
                excecao.getMessage()
        );

        verify(professorRepositorio)
                .existsByEmail(emailExistente);

        verify(professorRepositorio, never())
                .save(any(Professor.class));
    }

}
