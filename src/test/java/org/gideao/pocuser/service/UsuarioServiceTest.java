package org.gideao.pocuser.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.gideao.pocuser.domain.model.Usuario;
import org.gideao.pocuser.dto.LoginRequestDto;
import org.gideao.pocuser.dto.UsuarioRequestDto;
import org.gideao.pocuser.exception.CredenciaisInvalidasException;
import org.gideao.pocuser.exception.EmailJaCadastradoException;
import org.gideao.pocuser.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDto usuarioRequestDto;

    @BeforeEach
    void setUp() {
        usuarioRequestDto = new UsuarioRequestDto(
                "João Silva",
                "joao@email.com",
                "joao.silva",
                "Senha@123"
        );
    }

    @Test
    void deveCriarUsuarioComSenhaCriptografada() {
        when(usuarioRepository.existsByEmail(usuarioRequestDto.email())).thenReturn(false);
        when(usuarioRepository.existsByLogin(usuarioRequestDto.login())).thenReturn(false);
        when(passwordEncoder.encode(usuarioRequestDto.senha())).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        Usuario usuario = usuarioService.criar(usuarioRequestDto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("$2a$10$hash", captor.getValue().getSenha());
        assertEquals(1L, usuario.getId());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistir() {
        when(usuarioRepository.existsByEmail(usuarioRequestDto.email())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> usuarioService.criar(usuarioRequestDto));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveValidarLoginComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setLogin("joao.silva");
        usuario.setSenha("$2a$10$hash");

        when(usuarioRepository.findByLogin("joao.silva")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Senha@123", "$2a$10$hash")).thenReturn(true);

        assertDoesNotThrow(() -> usuarioService.validarLogin(new LoginRequestDto("joao.silva", "Senha@123")));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForInvalida() {
        Usuario usuario = new Usuario();
        usuario.setLogin("joao.silva");
        usuario.setSenha("$2a$10$hash");

        when(usuarioRepository.findByLogin("joao.silva")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaErrada", "$2a$10$hash")).thenReturn(false);

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> usuarioService.validarLogin(new LoginRequestDto("joao.silva", "SenhaErrada"))
        );
    }
}
