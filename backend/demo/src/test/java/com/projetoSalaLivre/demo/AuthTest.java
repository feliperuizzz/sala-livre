package com.projetoSalaLivre.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthTest {

    @Test
    void deveValidarEmailCorretamente() {

        String email = "teste@email.com";

        assertTrue(email.contains("@"));
        assertTrue(email.endsWith(".com"));
    }
}