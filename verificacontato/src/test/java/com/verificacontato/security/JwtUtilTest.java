package com.verificacontato.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    void deveGerarTokenValido() {
        String username = "teste@usuario.com";
        String token = JwtUtil.generateToken(username);

        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // tokens JWT geralmente começam com 'ey'
    }

    @Test
    void deveExtrairUsernameDoToken() {
        String username = "teste@usuario.com";
        String token = JwtUtil.generateToken(username);

        String usernameExtraido = JwtUtil.getUsernameFromToken(token);

        assertEquals(username, usernameExtraido);
    }

    @Test
    void deveValidarTokenValido() {
        String token = JwtUtil.generateToken("teste@usuario.com");

        assertTrue(JwtUtil.isTokenValid(token));
    }

    @Test
    void deveInvalidarTokenInvalido() {
        String tokenInvalido = "token.invalido.exemplo";

        assertFalse(JwtUtil.isTokenValid(tokenInvalido));
    }
}

