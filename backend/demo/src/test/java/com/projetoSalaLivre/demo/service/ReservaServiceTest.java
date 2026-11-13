package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.dto.ReservaRequestDTO;
import com.projetoSalaLivre.demo.dto.ReservaResponseDTO;
import com.projetoSalaLivre.demo.model.Bloco;
import com.projetoSalaLivre.demo.model.Perfil;
import com.projetoSalaLivre.demo.model.Periodo;
import com.projetoSalaLivre.demo.model.Professor;
import com.projetoSalaLivre.demo.model.Reserva;
import com.projetoSalaLivre.demo.model.Sala;
import com.projetoSalaLivre.demo.repositorio.ProfessorRepositorio;
import com.projetoSalaLivre.demo.repositorio.ReservaRepositorio;
import com.projetoSalaLivre.demo.repositorio.SalaRepositorio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepositorio reservaRepositorio;

    @Mock
    private SalaRepositorio salaRepositorio;

    @Mock
    private ProfessorRepositorio professorRepositorio;

    @InjectMocks
    private ReservaService reservaService;

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void t01_deveCriarReservaValidaAssociadaAoProfessorAutenticado() {
        String email = "professor.teste@exemplo.com";
        Long idProfessor = 7L;
        Long idSala = 10L;
        LocalDate data = LocalDate.now().plusDays(1);
        Periodo periodo = Periodo.MANHA;

        Professor professor = new Professor();
        professor.setId(idProfessor);
        professor.setNome("Professor Teste");
        professor.setEmail(email);
        professor.setPerfil(Perfil.PROFESSOR);
        professor.setAtivo(true);

        Bloco bloco = new Bloco();
        bloco.setId(3L);
        bloco.setCodigo("B");

        Sala sala = new Sala();
        sala.setId(idSala);
        sala.setCodigo("101");
        sala.setBloco(bloco);

        UsernamePasswordAuthenticationToken autenticacao =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"))
                );
        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        ReservaRequestDTO requisicao = new ReservaRequestDTO(idSala, data, periodo);

        when(professorRepositorio.findByEmail(email))
                .thenReturn(Optional.of(professor));
        when(salaRepositorio.findById(idSala))
                .thenReturn(Optional.of(sala));
        when(reservaRepositorio.existsBySalaIdAndDataAndPeriodo(idSala, data, periodo))
                .thenReturn(false);
        when(reservaRepositorio.save(any(Reserva.class)))
                .thenAnswer(invocacao -> {
                    Reserva reserva = invocacao.getArgument(0);
                    reserva.setId(99L);
                    return reserva;
                });

        ReservaResponseDTO resposta = reservaService.criarReserva(requisicao);

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepositorio).save(captor.capture());
        Reserva reservaSalva = captor.getValue();

        assertSame(professor, reservaSalva.getProfessor());
        assertSame(sala, reservaSalva.getSala());
        assertEquals(data, reservaSalva.getData());
        assertEquals(periodo, reservaSalva.getPeriodo());

        assertEquals(99L, resposta.id());
        assertEquals(idProfessor, resposta.professorId());
        assertEquals("Professor Teste", resposta.professorNome());
        assertEquals(idSala, resposta.salaId());
        assertEquals("B101", resposta.salaCodigo());
        assertEquals(data, resposta.data());
        assertEquals(periodo, resposta.periodo());

        verify(professorRepositorio).findByEmail(email);
        verify(salaRepositorio).findById(idSala);
        verify(reservaRepositorio)
                .existsBySalaIdAndDataAndPeriodo(idSala, data, periodo);
    }

    @Test
    void t02_deveImpedirReservaDuplicadaParaMesmaSalaDataEPeriodo() {
        String email = "professor.teste@exemplo.com";
        Long idSala = 10L;
        LocalDate data = LocalDate.now().plusDays(1);
        Periodo periodo = Periodo.MANHA;

        Professor professor = new Professor();
        professor.setId(7L);
        professor.setEmail(email);
        professor.setPerfil(Perfil.PROFESSOR);
        professor.setAtivo(true);

        Sala sala = new Sala();
        sala.setId(idSala);

        UsernamePasswordAuthenticationToken autenticacao =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"))
                );
        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        ReservaRequestDTO requisicao = new ReservaRequestDTO(idSala, data, periodo);

        when(professorRepositorio.findByEmail(email))
                .thenReturn(Optional.of(professor));
        when(salaRepositorio.findById(idSala))
                .thenReturn(Optional.of(sala));
        when(reservaRepositorio.existsBySalaIdAndDataAndPeriodo(idSala, data, periodo))
                .thenReturn(true);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> reservaService.criarReserva(requisicao)
        );

        assertEquals(
                "A sala já está reservada para a data e período selecionados.",
                excecao.getMessage()
        );
        verify(reservaRepositorio).existsBySalaIdAndDataAndPeriodo(idSala, data, periodo);
        verify(reservaRepositorio, never()).save(any(Reserva.class));
    }

    @Test
    void t03_deveImpedirReservaEmDataAnteriorAAtual() {
        Long idSala = 10L;
        LocalDate dataPassada = LocalDate.now().minusDays(1);
        ReservaRequestDTO requisicao = new ReservaRequestDTO(
                idSala,
                dataPassada,
                Periodo.MANHA
        );

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.criarReserva(requisicao)
        );

        assertEquals(
                "Não é permitido realizar reservas em datas anteriores à atual.",
                excecao.getMessage()
        );

        verifyNoInteractions(professorRepositorio, salaRepositorio, reservaRepositorio);
    }


    @Test
    void t04_devePermitirProfessorCancelarSuaPropriaReserva() {
        String email = "professor.teste@exemplo.com";
        Long idProfessor = 7L;
        Long idReserva = 42L;

        Professor professor = new Professor();
        professor.setId(idProfessor);
        professor.setEmail(email);
        professor.setPerfil(Perfil.PROFESSOR);
        professor.setAtivo(true);

        Reserva reserva = new Reserva();
        reserva.setId(idReserva);
        reserva.setProfessor(professor);

        UsernamePasswordAuthenticationToken autenticacao =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"))
                );
        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        when(professorRepositorio.findByEmail(email))
                .thenReturn(Optional.of(professor));
        when(reservaRepositorio.findById(idReserva))
                .thenReturn(Optional.of(reserva));

        assertDoesNotThrow(() -> reservaService.cancelarReserva(idReserva));

        verify(professorRepositorio).findByEmail(email);
        verify(reservaRepositorio).findById(idReserva);
        verify(reservaRepositorio).delete(same(reserva));
        verify(reservaRepositorio, never()).save(any(Reserva.class));
    }


    @Test
    void t05_deveImpedirProfessorCancelarReservaDeOutroUsuario() {
        String emailAutenticado = "professor2.teste@exemplo.com";
        Long idProfessorAutenticado = 8L;
        Long idProfessorProprietario = 7L;
        Long idReserva = 43L;

        Professor professorAutenticado = new Professor();
        professorAutenticado.setId(idProfessorAutenticado);
        professorAutenticado.setEmail(emailAutenticado);
        professorAutenticado.setPerfil(Perfil.PROFESSOR);
        professorAutenticado.setAtivo(true);

        Professor professorProprietario = new Professor();
        professorProprietario.setId(idProfessorProprietario);
        professorProprietario.setPerfil(Perfil.PROFESSOR);

        Reserva reservaDeOutroProfessor = new Reserva();
        reservaDeOutroProfessor.setId(idReserva);
        reservaDeOutroProfessor.setProfessor(professorProprietario);

        UsernamePasswordAuthenticationToken autenticacao =
                new UsernamePasswordAuthenticationToken(
                        emailAutenticado,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"))
                );
        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        when(professorRepositorio.findByEmail(emailAutenticado))
                .thenReturn(Optional.of(professorAutenticado));
        when(reservaRepositorio.findById(idReserva))
                .thenReturn(Optional.of(reservaDeOutroProfessor));

        AccessDeniedException excecao = assertThrows(
                AccessDeniedException.class,
                () -> reservaService.cancelarReserva(idReserva)
        );

        assertEquals(
                "Você não possui permissão para cancelar esta reserva.",
                excecao.getMessage()
        );

        verify(professorRepositorio).findByEmail(emailAutenticado);
        verify(reservaRepositorio).findById(idReserva);
        verify(reservaRepositorio, never()).delete(any(Reserva.class));
        verify(reservaRepositorio, never()).deleteById(anyLong());
    }


    @Test
    void t06_devePermitirAdministradorCancelarQualquerReserva() {

        String emailAdmin = "admin.teste@exemplo.com";
        Long idReserva = 44L;

        Professor administrador = new Professor();
        administrador.setId(1L);
        administrador.setNome("Administrador Teste");
        administrador.setEmail(emailAdmin);
        administrador.setPerfil(Perfil.ADMIN);
        administrador.setAtivo(true);

        Professor professorProprietario = new Professor();
        professorProprietario.setId(7L);
        professorProprietario.setPerfil(Perfil.PROFESSOR);

        Reserva reserva = new Reserva();
        reserva.setId(idReserva);
        reserva.setProfessor(professorProprietario);

        UsernamePasswordAuthenticationToken autenticacao =
                new UsernamePasswordAuthenticationToken(
                        emailAdmin,
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(autenticacao);

        when(professorRepositorio.findByEmail(emailAdmin))
                .thenReturn(Optional.of(administrador));

        when(reservaRepositorio.findById(idReserva))
                .thenReturn(Optional.of(reserva));

        assertDoesNotThrow(
                () -> reservaService.cancelarReserva(idReserva)
        );

        verify(professorRepositorio).findByEmail(emailAdmin);

        verify(reservaRepositorio).findById(idReserva);

        verify(reservaRepositorio).delete(same(reserva));

        verify(reservaRepositorio, never())
                .save(any(Reserva.class));
    }


}
