package com.verificacontato.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verificacontato.model.Usuario;
import com.verificacontato.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // desativa filtros de segurança

class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {
        Usuario usuario = new Usuario(null, "Teste", "teste0@email.com", "senha123", null);
        Usuario salvo = new Usuario(1L, "Teste", "teste0@email.com", "senhaCriptografada", LocalDateTime.now());

        when(usuarioService.cadastrarUsuario(usuario)).thenReturn(salvo);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("teste0@email.com"));
    }

    @Test
    void deveRetornarErroAoCadastrarUsuarioExistente() throws Exception {
        Usuario usuario = new Usuario(null, "Teste", "teste0@email.com", "senha123", null);

        when(usuarioService.cadastrarUsuario(usuario))
            .thenThrow(new RuntimeException("E-mail já cadastrado"));

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("E-mail já cadastrado"));
    }

    @Test
    void deveRealizarLoginComSucessoERetornarToken() throws Exception {
        Usuario usuario = new Usuario(null, "Teste", "teste0@email.com", "senha123", null);
        String token = "fake.jwt.token";

        when(usuarioService.login(usuario.getEmail(), usuario.getSenha())).thenReturn(token);

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(token));
    }
}
