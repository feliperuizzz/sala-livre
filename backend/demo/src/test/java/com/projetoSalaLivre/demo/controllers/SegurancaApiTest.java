
package com.projetoSalaLivre.demo.controllers;

import com.projetoSalaLivre.demo.dto.SalaDisponibilidadeDTO;
import com.projetoSalaLivre.demo.model.Periodo;
import com.projetoSalaLivre.demo.security.CustomUserDetailsService;
import com.projetoSalaLivre.demo.security.JwtService;
import com.projetoSalaLivre.demo.security.SecurityConfig;
import com.projetoSalaLivre.demo.service.BlocoService;
import com.projetoSalaLivre.demo.service.ReservaService;
import com.projetoSalaLivre.demo.service.SalaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.time.LocalDate;
import java.util.List;
import com.projetoSalaLivre.demo.model.Perfil;
import com.projetoSalaLivre.demo.model.Professor;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.projetoSalaLivre.demo.dto.BlocoResponseDTO;

@WebMvcTest(controllers = {
        SalaController.class,
        ReservaController.class,
        AdminBlocoController.class
})
@Import(SecurityConfig.class)
class SegurancaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaService salaService;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private BlocoService blocoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void t13_devePermitirConsultaPublicaDeDisponibilidadeSemJwt()
            throws Exception {

        LocalDate data = LocalDate.now().plusDays(1);
        Periodo periodo = Periodo.MANHA;

        List<SalaDisponibilidadeDTO> salas = List.of(
                new SalaDisponibilidadeDTO(
                        10L, "A101", "A", true,
                        null, null, null
                ),
                new SalaDisponibilidadeDTO(
                        11L, "A102", "A", false,
                        55L, 7L, "Professor Teste"
                )
        );

        when(salaService.consultarDisponibilidade(data, periodo))
                .thenReturn(salas);

        mockMvc.perform(
                        get("/api/salas/disponibilidade")
                                .param("data", data.toString())
                                .param("periodo", "MANHA")
                                .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo").value("A101"))
                .andExpect(jsonPath("$[0].disponivel").value(true))
                .andExpect(jsonPath("$[1].codigo").value("A102"))
                .andExpect(jsonPath("$[1].disponivel").value(false))
                .andExpect(jsonPath("$[1].professor")
                        .value("Professor Teste"));

        verify(salaService)
                .consultarDisponibilidade(data, periodo);
    }


    @Test
    void t14_deveBloquearCriacaoDeReservaSemJwt()
            throws Exception {

        LocalDate data = LocalDate.now().plusDays(1);

        String corpoJson = """
            {
                "salaId": 10,
                "data": "%s",
                "periodo": "MANHA"
            }
            """.formatted(data);

        mockMvc.perform(
                        post("/api/reservas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(corpoJson)
                )

                .andExpect(status().isUnauthorized())

                .andExpect(jsonPath("$.erro")
                        .value("Autenticação necessária."));

        verifyNoInteractions(reservaService);
    }


    @Test
    void t15_deveNegarAcessoDeProfessorAoEndpointAdministrativo()
            throws Exception {

        String email = "professor@teste.com";
        String token = "123456";

        Professor professor = new Professor();
        professor.setId(7L);
        professor.setNome("Professor Teste");
        professor.setEmail(email);
        professor.setPerfil(Perfil.PROFESSOR);
        professor.setAtivo(true);

        when(jwtService.extrairEmail(token))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(professor);

        when(jwtService.tokenValido(token, professor))
                .thenReturn(true);

        mockMvc.perform(
                        get("/api/admin/blocos")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro")
                        .value("Acesso negado."));

        verify(jwtService).extrairEmail(token);

        verify(userDetailsService)
                .loadUserByUsername(email);

        verify(jwtService)
                .tokenValido(token, professor);

        verifyNoInteractions(blocoService);
    }


    @Test
    void t16_devePermitirAdministradorAcessarEndpointAdministrativo()
            throws Exception {

        String email = "admin@teste.com";
        String token = "123456";

        Professor administrador = new Professor();
        administrador.setId(1L);
        administrador.setNome("Administrador Teste");
        administrador.setEmail(email);
        administrador.setPerfil(Perfil.ADMIN);
        administrador.setAtivo(true);

        when(jwtService.extrairEmail(token))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(administrador);

        when(jwtService.tokenValido(token, administrador))
                .thenReturn(true);

        List<BlocoResponseDTO> blocos = List.of(
                new BlocoResponseDTO(1L, "A"),
                new BlocoResponseDTO(2L, "B")
        );

        when(blocoService.listar())
                .thenReturn(blocos);

        mockMvc.perform(
                        get("/api/admin/blocos")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].codigo").value("A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].codigo").value("B"));

        verify(jwtService).extrairEmail(token);

        verify(userDetailsService)
                .loadUserByUsername(email);

        verify(jwtService)
                .tokenValido(token, administrador);

        verify(blocoService).listar();
    }

}
