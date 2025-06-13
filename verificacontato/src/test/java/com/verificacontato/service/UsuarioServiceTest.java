package com.verificacontato.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.verificacontato.model.Usuario;
import com.verificacontato.repository.UsuarioRepository;
import com.verificacontato.security.JwtUtil;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioExemplo;

    @BeforeEach
    void setup() {
        usuarioExemplo = new Usuario();
        usuarioExemplo.setId(1L);
        usuarioExemplo.setNome("Usuário Teste");
        usuarioExemplo.setEmail("teste@exemplo.com");
        usuarioExemplo.setSenha("senha123");
        usuarioExemplo.setDataCadastro(LocalDateTime.now());
    }

    @Test
    void cadastrarUsuario_deveSalvarQuandoEmailNaoExiste() {
        when(usuarioRepository.findByEmail(usuarioExemplo.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioSalvo = usuarioService.cadastrarUsuario(usuarioExemplo);

        assertNotNull(usuarioSalvo.getSenha());
        assertNotEquals("senha123", usuarioSalvo.getSenha()); // senha deve estar codificada
        assertNotNull(usuarioSalvo.getDataCadastro());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void cadastrarUsuario_deveLancarExcecaoQuandoEmailExistente() {
        when(usuarioRepository.findByEmail(usuarioExemplo.getEmail())).thenReturn(Optional.of(usuarioExemplo));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            usuarioService.cadastrarUsuario(usuarioExemplo);
        });

        assertEquals("E-mail já cadastrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void login_deveRetornarTokenQuandoCredenciaisValidas() {
        String senhaCodificada = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("senha123");
        usuarioExemplo.setSenha(senhaCodificada);

        when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.of(usuarioExemplo));

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.generateToken("teste@exemplo.com")).thenReturn("tokenFake123");

            String token = usuarioService.login("teste@exemplo.com", "senha123");
            assertEquals("tokenFake123", token);

            jwtUtilMock.verify(() -> JwtUtil.generateToken("teste@exemplo.com"));
        }
    }

    @Test
    void login_deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findByEmail("naoexiste@exemplo.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            usuarioService.login("naoexiste@exemplo.com", "senha123");
        });

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void login_deveLancarExcecaoQuandoSenhaIncorreta() {
        String senhaCodificada = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("senhaCorreta");
        usuarioExemplo.setSenha(senhaCodificada);

        when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.of(usuarioExemplo));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            usuarioService.login("teste@exemplo.com", "senhaErrada");
        });

        assertEquals("Credenciais inválidas", ex.getMessage());
    }

    @Test
    void buscarPorEmail_deveRetornarUsuarioQuandoEncontrar() {
        when(usuarioRepository.findByEmail("teste@exemplo.com")).thenReturn(Optional.of(usuarioExemplo));

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("teste@exemplo.com");

        assertTrue(resultado.isPresent());
        assertEquals("teste@exemplo.com", resultado.get().getEmail());
    }

    @Test
    void buscarPorEmail_deveRetornarVazioQuandoNaoEncontrar() {
        when(usuarioRepository.findByEmail("naoexiste@exemplo.com")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("naoexiste@exemplo.com");

        assertFalse(resultado.isPresent());
    }
}

