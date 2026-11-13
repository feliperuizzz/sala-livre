package com.projetoSalaLivre.demo.service;

import com.projetoSalaLivre.demo.dto.ReservaRequestDTO;
import com.projetoSalaLivre.demo.dto.ReservaResponseDTO;
import com.projetoSalaLivre.demo.model.Professor;
import com.projetoSalaLivre.demo.model.Reserva;
import com.projetoSalaLivre.demo.model.Sala;
import com.projetoSalaLivre.demo.repositorio.ProfessorRepositorio;
import com.projetoSalaLivre.demo.repositorio.ReservaRepositorio;
import com.projetoSalaLivre.demo.repositorio.SalaRepositorio;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projetoSalaLivre.demo.model.Perfil;

import java.time.LocalDate;

@Service
public class ReservaService {

    private final ReservaRepositorio reservaRepositorio;
    private final SalaRepositorio salaRepositorio;
    private final ProfessorRepositorio professorRepositorio;


    public ReservaService(
            ReservaRepositorio reservaRepositorio,
            SalaRepositorio salaRepositorio,
            ProfessorRepositorio professorRepositorio
    ) {
        this.reservaRepositorio = reservaRepositorio;
        this.salaRepositorio = salaRepositorio;
        this.professorRepositorio = professorRepositorio;
    }

    @Transactional
    public ReservaResponseDTO criarReserva(ReservaRequestDTO request) {

        validarDadosReserva(request);

        Professor professor = obterProfessorAutenticado();

        Sala sala = salaRepositorio.findById(request.salaId())
                .orElseThrow(
                        () -> new RuntimeException("Sala não encontrada.")
                );

        boolean jaExisteReserva =
                reservaRepositorio.existsBySalaIdAndDataAndPeriodo(
                        sala.getId(),
                        request.data(),
                        request.periodo()
                );

        if (jaExisteReserva) {
            throw new IllegalStateException(
                    "A sala já está reservada para a data e período selecionados."
            );
        }

        Reserva reserva = new Reserva();

        reserva.setData(request.data());
        reserva.setPeriodo(request.periodo());
        reserva.setProfessor(professor);
        reserva.setSala(sala);

        try {

            Reserva reservaSalva =
                    reservaRepositorio.save(reserva);

            return converterParaDTO(reservaSalva);

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "A sala já está reservada para a data e período selecionados."
            );
        }
    }

    @Transactional
    public void cancelarReserva(Long reservaId) {

        Professor professor = obterProfessorAutenticado();

        Reserva reserva = reservaRepositorio.findById(reservaId)
                .orElseThrow(
                        () -> new RuntimeException("Reserva não encontrada.")
                );

        boolean administrador =
                professor.getPerfil() == Perfil.ADMIN;

        boolean proprietario =
                reserva.getProfessor()
                        .getId()
                        .equals(professor.getId());

        if (!administrador && !proprietario) {

            throw new AccessDeniedException(
                    "Você não possui permissão para cancelar esta reserva."
            );
        }

        reservaRepositorio.delete(reserva);
    }

    private void validarDadosReserva(ReservaRequestDTO request) {

        if (request == null) {
            throw new RuntimeException(
                    "Os dados da reserva devem ser informados."
            );
        }

        if (request.salaId() == null) {
            throw new RuntimeException(
                    "A sala deve ser informada."
            );
        }

        if (request.data() == null) {
            throw new RuntimeException(
                    "A data deve ser informada."
            );
        }

        if (request.periodo() == null) {
            throw new RuntimeException(
                    "O período deve ser informado."
            );
        }

        if (request.data().isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Não é permitido realizar reservas em datas anteriores à atual."
            );
        }
    }

    private Professor obterProfessorAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            throw new AccessDeniedException(
                    "Professor não autenticado."
            );
        }

        String email = authentication.getName();

        return professorRepositorio.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Professor autenticado não encontrado."
                        )
                );
    }

    private ReservaResponseDTO converterParaDTO(
            Reserva reserva
    ) {

        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getSala().getId(),
                reserva.getSala().getBloco().getCodigo()
                        + reserva.getSala().getCodigo(),
                reserva.getSala()
                        .getBloco()
                        .getCodigo(),
                reserva.getData(),
                reserva.getPeriodo(),
                reserva.getProfessor().getId(),
                reserva.getProfessor().getNome()
        );
    }
}