package org.gideao.pocuser.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gideao.pocuser.domain.model.Usuario;
import org.gideao.pocuser.dto.LoginRequestDto;
import org.gideao.pocuser.dto.UsuarioRequestDto;
import org.gideao.pocuser.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UsuarioController.class)
@Import(ApiExceptionHandler.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCriarUsuarioERetornarCreated() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setLogin("joao.silva");

        when(usuarioService.criar(any(UsuarioRequestDto.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UsuarioRequestDto(
                                "João Silva",
                                "joao@email.com",
                                "joao.silva",
                                "Senha@123"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/usuarios/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void deveAtualizarUsuarioERetornarOk() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Atualizado");
        usuario.setEmail("joao.atualizado@email.com");
        usuario.setLogin("joao.atualizado");

        when(usuarioService.atualizar(any(Long.class), any(UsuarioRequestDto.class))).thenReturn(usuario);

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UsuarioRequestDto(
                                "João Atualizado",
                                "joao.atualizado@email.com",
                                "joao.atualizado",
                                "NovaSenha@123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("joao.atualizado"));
    }

    @Test
    void deveDeletarUsuarioERetornarNoContent() throws Exception {
        doNothing().when(usuarioService).deletar(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveValidarLoginERetornarOk() throws Exception {
        doNothing().when(usuarioService).validarLogin(any(LoginRequestDto.class));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDto("joao.silva", "Senha@123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Login realizado com sucesso"));
    }
}
