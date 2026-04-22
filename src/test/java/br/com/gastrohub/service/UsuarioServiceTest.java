package br.com.gastrohub.service;

import br.com.gastrohub.dto.request.*;
import br.com.gastrohub.dto.response.LoginResponse;
import br.com.gastrohub.dto.response.UsuarioResponse;
import br.com.gastrohub.entity.Usuario;
import br.com.gastrohub.enums.TipoUsuarioEnum;
import br.com.gastrohub.exception.AcessoNegadoException;
import br.com.gastrohub.exception.DadosJaCadastradosException;
import br.com.gastrohub.exception.LoginOuSenhaInvalidosException;
import br.com.gastrohub.exception.SenhaAtualInvalidaException;
import br.com.gastrohub.exception.UsuarioNaoEncontradoException;
import br.com.gastrohub.repository.UsuarioRepository;
import br.com.gastrohub.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da camada de serviço de Usuário")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder codificadorDeSenha;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioBase;

    @BeforeEach
    void configurarDadosBase() {
        usuarioBase = Usuario.builder()
                .id(1L)
                .nome("Roberto Rodriguez")
                .email("roberto.rodriguez@email.com")
                .login("rrodriguez")
                .senha("$argon2id$v=19$m=65536,t=3,p=1$hashedPassword")
                .tipo(TipoUsuarioEnum.CLIENTE)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAlteracao(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void limparSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarComo(String login, String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                login, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Nested
    @DisplayName("Criar Usuário")
    class CriarUsuario {

        @Test
        @DisplayName("Deve criar usuário com sucesso quando dados válidos são fornecidos")
        void deveCriarUsuarioComSucesso() {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "Roberto Rodriguez", "roberto.rodriguez@email.com", "rrodriguez", "senha123", TipoUsuarioEnum.CLIENTE
            );

            when(usuarioRepository.existsByLogin(anyString())).thenReturn(false);
            when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
            when(codificadorDeSenha.encode(anyString())).thenReturn("$argon2id$v=19$m=65536,t=3,p=1$hashedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBase);

            UsuarioResponse response = usuarioService.criarUsuario(request);

            assertThat(response).isNotNull();
            assertThat(response.nome()).isEqualTo("Roberto Rodriguez");
            assertThat(response.login()).isEqualTo("rrodriguez");
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando login já está em uso")
        void deveLancarExcecaoQuandoLoginJaExiste() {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "Roberto Rodriguez", "roberto.rodriguez@email.com", "rrodriguez", "senha123", TipoUsuarioEnum.CLIENTE
            );

            when(usuarioRepository.existsByLogin("rrodriguez")).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.criarUsuario(request))
                    .isInstanceOf(DadosJaCadastradosException.class)
                    .hasMessageContaining("rrodriguez");
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já está em uso")
        void deveLancarExcecaoQuandoEmailJaExiste() {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "Roberto Rodriguez", "roberto.rodriguez@email.com", "rrodriguez", "senha123", TipoUsuarioEnum.CLIENTE
            );

            when(usuarioRepository.existsByLogin(anyString())).thenReturn(false);
            when(usuarioRepository.existsByEmail("roberto.rodriguez@email.com")).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.criarUsuario(request))
                    .isInstanceOf(DadosJaCadastradosException.class)
                    .hasMessageContaining("roberto.rodriguez@email.com");
        }
    }

    @Nested
    @DisplayName("Buscar Usuário")
    class BuscarUsuario {

        @Test
        @DisplayName("Deve retornar usuário quando ADMIN busca qualquer ID")
        void deveRetornarUsuarioQuandoAdminBusca() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

            UsuarioResponse response = usuarioService.buscarUsuarioPorId(1L);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.nome()).isEqualTo("Roberto Rodriguez");
        }

        @Test
        @DisplayName("Deve retornar usuário quando CLIENTE busca o próprio ID")
        void deveRetornarUsuarioQuandoClienteBuscaOProprio() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

            UsuarioResponse response = usuarioService.buscarUsuarioPorId(1L);

            assertThat(response).isNotNull();
            assertThat(response.login()).isEqualTo("rrodriguez");
        }

        @Test
        @DisplayName("Deve retornar usuário quando CLIENTE acessa dados de outro usuário")
        void deveRetornarUsuarioQuandoClienteAcessaDadosDeOutro() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

            UsuarioResponse response = usuarioService.buscarUsuarioPorId(1L);

            assertThat(response).isNotNull();
            assertThat(response.login()).isEqualTo("rrodriguez");
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.buscarUsuarioPorId(99L))
                    .isInstanceOf(UsuarioNaoEncontradoException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("Deve listar todos os usuários")
        void deveListarTodosOsUsuarios() {
            when(usuarioRepository.findAll()).thenReturn(List.of(usuarioBase));

            List<UsuarioResponse> lista = usuarioService.listarTodosUsuarios();

            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).nome()).isEqualTo("Roberto Rodriguez");
        }

        @Test
        @DisplayName("Deve buscar usuários por nome contendo texto")
        void deveBuscarUsuariosPorNome() {
            when(usuarioRepository.buscarPorNomeFlexivel("Roberto")).thenReturn(List.of(usuarioBase));

            List<UsuarioResponse> lista = usuarioService.buscarPorNome("Roberto");

            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).nome()).isEqualTo("Roberto Rodriguez");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nenhum usuário contém o nome")
        void deveRetornarVazioQuandoNenhumNomeMatcha() {
            when(usuarioRepository.buscarPorNomeFlexivel("Inexistente")).thenReturn(new ArrayList<>());

            List<UsuarioResponse> lista = usuarioService.buscarPorNome("Inexistente");

            assertThat(lista).isEmpty();
        }
    }

    @Nested
    @DisplayName("Atualizar Usuário")
    class AtualizarUsuario {

        @Test
        @DisplayName("Deve atualizar quando ADMIN modifica qualquer usuário")
        void deveAtualizarQuandoAdminModifica() {
            autenticarComo("admin", "ADMIN");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Roberto Santos", null);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBase);

            UsuarioResponse response = usuarioService.atualizarDadosDoUsuario(1L, request);

            assertThat(response).isNotNull();
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve atualizar quando usuário modifica o próprio cadastro")
        void deveAtualizarQuandoUsuarioModificaOProprio() {
            autenticarComo("rrodriguez", "CLIENTE");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Roberto Santos", null);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBase);

            UsuarioResponse response = usuarioService.atualizarDadosDoUsuario(1L, request);

            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar AcessoNegadoException quando usuário tenta modificar outro")
        void deveLancarExcecaoQuandoUsuarioModificaOutro() {
            autenticarComo("outroUsuario", "CLIENTE");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Roberto Santos", null);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

            assertThatThrownBy(() -> usuarioService.atualizarDadosDoUsuario(1L, request))
                    .isInstanceOf(AcessoNegadoException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
        void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
            autenticarComo("admin", "ADMIN");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Roberto Santos", null);

            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.atualizarDadosDoUsuario(99L, request))
                    .isInstanceOf(UsuarioNaoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("Excluir Usuário")
    class ExcluirUsuario {

        @Test
        @DisplayName("Deve excluir usuário com sucesso quando ADMIN solicita")
        void deveExcluirUsuarioComSucessoQuandoAdmin() {
            autenticarComo("admin", "ADMIN");
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

            usuarioService.excluirUsuario(1L);

            verify(usuarioRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve excluir a própria conta com sucesso")
        void deveExcluirPropriaContaComSucesso() {
            autenticarComo("rrodriguez", "CLIENTE");
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

            usuarioService.excluirUsuario(1L);

            verify(usuarioRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lançar AcessoNegadoException quando usuário tenta excluir outro")
        void deveLancarExcecaoQuandoClienteTentaExcluirOutro() {
            autenticarComo("outroUsuario", "CLIENTE");
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

            assertThatThrownBy(() -> usuarioService.excluirUsuario(1L))
                    .isInstanceOf(AcessoNegadoException.class);

            verify(usuarioRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao excluir usuário inexistente")
        void deveLancarExcecaoAoExcluirUsuarioInexistente() {
            autenticarComo("admin", "ADMIN");
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.excluirUsuario(99L))
                    .isInstanceOf(UsuarioNaoEncontradoException.class);

            verify(usuarioRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Trocar Senha")
    class TrocarSenha {

        @Test
        @DisplayName("Deve trocar senha com sucesso quando o próprio usuário solicita")
        void deveTrocarSenhaComSucesso() {
            autenticarComo("rrodriguez", "CLIENTE");
            TrocarSenhaRequest request = new TrocarSenhaRequest("senhaAtual123", "novaSenha456");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
            when(codificadorDeSenha.matches("senhaAtual123", usuarioBase.getSenha())).thenReturn(true);
            when(codificadorDeSenha.encode("novaSenha456")).thenReturn("$argon2id$v=19$m=65536,t=3,p=1$novoHash");

            usuarioService.trocarSenhaDoUsuario(1L, request);

            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve trocar senha quando ADMIN altera senha de qualquer usuário")
        void deveAdminPoderTrocarSenhaDeQualquerUsuario() {
            autenticarComo("admin", "ADMIN");
            TrocarSenhaRequest request = new TrocarSenhaRequest("ignorado", "novaSenha456");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
            when(codificadorDeSenha.encode("novaSenha456")).thenReturn("$argon2id$v=19$m=65536,t=3,p=1$novoHash");

            usuarioService.trocarSenhaDoUsuario(1L, request);

            verify(codificadorDeSenha, never()).matches(any(), any());
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve exigir confirmação da senha atual quando ADMIN altera a própria senha")
        void deveExigirSenhaAtualQuandoAdminAlteraPropraSenha() {
            Usuario adminUsuario = Usuario.builder()
                    .id(2L)
                    .login("admin")
                    .senha("$argon2id$v=19$m=65536,t=3,p=1$adminHash")
                    .tipo(TipoUsuarioEnum.ADMIN)
                    .build();
            autenticarComo("admin", "ADMIN");
            TrocarSenhaRequest request = new TrocarSenhaRequest("senhaAtualAdmin", "novaSenha456");

            String senhaOriginal = adminUsuario.getSenha();
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(adminUsuario));
            when(codificadorDeSenha.matches("senhaAtualAdmin", senhaOriginal)).thenReturn(true);
            when(codificadorDeSenha.encode("novaSenha456")).thenReturn("$argon2id$v=19$m=65536,t=3,p=1$novoHash");

            usuarioService.trocarSenhaDoUsuario(2L, request);

            verify(codificadorDeSenha).matches("senhaAtualAdmin", senhaOriginal);
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando senha atual está incorreta")
        void deveLancarExcecaoQuandoSenhaAtualEstaErrada() {
            autenticarComo("rrodriguez", "CLIENTE");
            TrocarSenhaRequest request = new TrocarSenhaRequest("senhaErrada", "novaSenha456");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
            when(codificadorDeSenha.matches("senhaErrada", usuarioBase.getSenha())).thenReturn(false);

            assertThatThrownBy(() -> usuarioService.trocarSenhaDoUsuario(1L, request))
                    .isInstanceOf(SenhaAtualInvalidaException.class);
        }
    }

    @Nested
    @DisplayName("Validar Login")
    class ValidarLogin {

        @Test
        @DisplayName("Deve retornar token JWT quando login e senha estão corretos")
        void deveRetornarTokenQuandoLoginESenhaEstaoCorretos() {
            ValidarLoginRequest request = new ValidarLoginRequest("rrodriguez", "senha123");

            when(usuarioRepository.findByLogin("rrodriguez")).thenReturn(Optional.of(usuarioBase));
            when(codificadorDeSenha.matches("senha123", usuarioBase.getSenha())).thenReturn(true);
            when(jwtService.gerarToken("rrodriguez")).thenReturn("eyJhbGciOiJIUzI1NiJ9.token.assinatura");
            when(jwtService.calcularDataExpiracao()).thenReturn(LocalDateTime.now().plusMinutes(30));

            LoginResponse response = usuarioService.validarLogin(request);

            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo("eyJhbGciOiJIUzI1NiJ9.token.assinatura");
            assertThat(response.tipo()).isEqualTo("Bearer");
            assertThat(response.expiracao()).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar exceção quando login não existe")
        void deveLancarExcecaoQuandoLoginNaoExiste() {
            ValidarLoginRequest request = new ValidarLoginRequest("loginInexistente", "senha123");

            when(usuarioRepository.findByLogin("loginInexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.validarLogin(request))
                    .isInstanceOf(LoginOuSenhaInvalidosException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando senha está incorreta")
        void deveLancarExcecaoQuandoSenhaEstaIncorreta() {
            ValidarLoginRequest request = new ValidarLoginRequest("rrodriguez", "senhaErrada");

            when(usuarioRepository.findByLogin("rrodriguez")).thenReturn(Optional.of(usuarioBase));
            when(codificadorDeSenha.matches("senhaErrada", usuarioBase.getSenha())).thenReturn(false);

            assertThatThrownBy(() -> usuarioService.validarLogin(request))
                    .isInstanceOf(LoginOuSenhaInvalidosException.class);
        }
    }
}
