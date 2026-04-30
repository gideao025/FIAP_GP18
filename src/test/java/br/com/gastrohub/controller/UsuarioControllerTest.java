package br.com.gastrohub.controller;

import br.com.gastrohub.dto.request.*;
import br.com.gastrohub.dto.response.LoginResponse;
import br.com.gastrohub.dto.response.UsuarioResponse;
import br.com.gastrohub.enums.TipoUsuarioEnum;
import br.com.gastrohub.config.SegurancaConfig;
import br.com.gastrohub.exception.AcessoNegadoException;
import br.com.gastrohub.exception.GlobalExceptionHandler;
import br.com.gastrohub.exception.LoginOuSenhaInvalidosException;
import br.com.gastrohub.exception.SenhaAtualInvalidaException;
import br.com.gastrohub.exception.UsuarioNaoEncontradoException;
import br.com.gastrohub.security.JwtService;
import br.com.gastrohub.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import({SegurancaConfig.class, GlobalExceptionHandler.class})
@DisplayName("Testes da camada de controller de Usuário")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private UsuarioResponse usuarioResponse;

    @BeforeEach
    void configurarDadosBase() {
        usuarioResponse = new UsuarioResponse(
                1L,
                "Roberto Rodriguez",
                "roberto.rodriguez@email.com",
                "rrodriguez",
                TipoUsuarioEnum.CLIENTE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /usuarios")
    class CriarUsuario {

        @Test
        @DisplayName("Deve retornar 201 ao criar usuário com dados válidos")
        void deveRetornar201AoCriarUsuarioComDadosValidos() throws Exception {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "Roberto Rodriguez",
                    "roberto.rodriguez@email.com",
                    "rrodriguez",
                    "senha123",
                    TipoUsuarioEnum.CLIENTE
            );

            when(usuarioService.criarUsuario(any())).thenReturn(usuarioResponse);

            mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nome").value("Roberto Rodriguez"))
                    .andExpect(jsonPath("$.login").value("rrodriguez"))
                    .andExpect(jsonPath("$.senha").doesNotExist());
        }

        @Test
        @DisplayName("Deve retornar 400 quando dados obrigatórios estão ausentes")
        void deveRetornar400QuandoDadosObrigatoriosEstaoAusentes() throws Exception {
            CriarUsuarioRequest requestInvalido = new CriarUsuarioRequest(null, null, null, null, null);

            mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /usuarios/{id}")
    class BuscarUsuarioPorId {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar usuário por ID existente")
        void deveRetornar200AoBuscarUsuarioPorId() throws Exception {
            when(usuarioService.buscarUsuarioPorId(1L)).thenReturn(usuarioResponse);

            mockMvc.perform(get("/v1/usuarios/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nome").value("Roberto Rodriguez"));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 404 quando ID não existe")
        void deveRetornar404QuandoIdNaoExiste() throws Exception {
            when(usuarioService.buscarUsuarioPorId(99L))
                    .thenThrow(new UsuarioNaoEncontradoException(99L));

            mockMvc.perform(get("/v1/usuarios/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Usuário não encontrado"));
        }
    }

    @Nested
    @DisplayName("GET /v1/usuarios")
    class ListarUsuarios {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 com lista de usuários para qualquer usuário autenticado")
        void deveRetornar200ComListaDeUsuarios() throws Exception {
            when(usuarioService.listarTodosUsuarios()).thenReturn(List.of(usuarioResponse));

            mockMvc.perform(get("/v1/usuarios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value("Roberto Rodriguez"));
        }
    }

    @Nested
    @DisplayName("GET /v1/usuarios/buscar")
    class BuscarPorNome {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 com usuários encontrados pelo nome")
        void deveRetornar200ComUsuariosEncontradosPeloNome() throws Exception {
            when(usuarioService.buscarPorNome("Roberto")).thenReturn(List.of(usuarioResponse));

            mockMvc.perform(get("/v1/usuarios/buscar").param("nome", "Roberto"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value("Roberto Rodriguez"));
        }

    }

    @Nested
    @DisplayName("PUT /usuarios/{id}")
    class AtualizarUsuario {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Deve retornar 200 quando ADMIN atualiza qualquer usuário")
        void deveRetornar200QuandoAdminAtualiza() throws Exception {
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Roberto Santos", null);
            when(usuarioService.atualizarDadosDoUsuario(eq(1L), any())).thenReturn(usuarioResponse);

            mockMvc.perform(put("/v1/usuarios/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("Deve retornar 403 quando CLIENTE tenta atualizar outro usuário")
        void deveRetornar403QuandoClienteAtualizaOutro() throws Exception {
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Roberto Santos", null);
            when(usuarioService.atualizarDadosDoUsuario(eq(1L), any()))
                    .thenThrow(new AcessoNegadoException());

            mockMvc.perform(put("/v1/usuarios/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /usuarios/{id}")
    class ExcluirUsuario {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 204 ao excluir usuário com sucesso")
        void deveRetornar204AoExcluirUsuario() throws Exception {
            doNothing().when(usuarioService).excluirUsuario(1L);

            mockMvc.perform(delete("/v1/usuarios/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("Deve retornar 403 quando tenta excluir outro usuário")
        void deveRetornar403QuandoTentaExcluirOutro() throws Exception {
            doThrow(new AcessoNegadoException()).when(usuarioService).excluirUsuario(1L);

            mockMvc.perform(delete("/v1/usuarios/1"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /usuarios/{id}/senha")
    class TrocarSenha {

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("Deve retornar 204 ao trocar própria senha com sucesso")
        void deveRetornar204AoTrocarSenhaComSucesso() throws Exception {
            TrocarSenhaRequest request = new TrocarSenhaRequest("senhaAtual123", "novaSenha456");
            doNothing().when(usuarioService).trocarSenhaDoUsuario(eq(1L), any());

            mockMvc.perform(patch("/v1/usuarios/1/senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("Deve retornar 422 quando senha atual está incorreta")
        void deveRetornar422QuandoSenhaAtualEstaIncorreta() throws Exception {
            TrocarSenhaRequest request = new TrocarSenhaRequest("senhaErrada", "novaSenha456");
            doThrow(new SenhaAtualInvalidaException())
                    .when(usuarioService).trocarSenhaDoUsuario(eq(1L), any());

            mockMvc.perform(patch("/v1/usuarios/1/senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Acesso não autenticado")
    class AcessoNaoAutenticado {

        @Test
        @DisplayName("Deve retornar 401 ao acessar endpoint protegido sem token")
        void deveRetornar401SemToken() throws Exception {
            mockMvc.perform(get("/v1/usuarios"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /usuarios/login")
    class ValidarLogin {

        @Test
        @DisplayName("Deve retornar 200 com token JWT quando login e senha estão corretos")
        void deveRetornar200QuandoLoginESenhaEstaoCorretos() throws Exception {
            ValidarLoginRequest request = new ValidarLoginRequest("rrodriguez", "senha123");
            LoginResponse loginResponse = new LoginResponse(
                    "eyJhbGciOiJIUzI1NiJ9.token.assinatura",
                    "Bearer",
                    LocalDateTime.now().plusMinutes(30)
            );
            when(usuarioService.validarLogin(any())).thenReturn(loginResponse);

            mockMvc.perform(post("/v1/usuarios/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.tipo").value("Bearer"));
        }

        @Test
        @DisplayName("Deve retornar 401 quando login ou senha estão incorretos")
        void deveRetornar401QuandoLoginOuSenhaEstaoIncorretos() throws Exception {
            ValidarLoginRequest request = new ValidarLoginRequest("rrodriguez", "senhaErrada");
            when(usuarioService.validarLogin(any())).thenThrow(new LoginOuSenhaInvalidosException());

            mockMvc.perform(post("/v1/usuarios/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }
}
