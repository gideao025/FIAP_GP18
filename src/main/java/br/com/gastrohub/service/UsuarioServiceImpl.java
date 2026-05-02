package br.com.gastrohub.service;

import br.com.gastrohub.dto.request.*;
import br.com.gastrohub.dto.response.LoginResponse;
import br.com.gastrohub.dto.response.UsuarioResponse;
import br.com.gastrohub.entity.Usuario;
import br.com.gastrohub.enums.TipoUsuarioEnum;
import br.com.gastrohub.exception.AcessoNegadoException;
import br.com.gastrohub.exception.DadosJaCadastradosException;
import br.com.gastrohub.exception.LoginOuSenhaInvalidosException;
import br.com.gastrohub.exception.OperacaoNaoPermitidaException;
import br.com.gastrohub.exception.SenhaAtualInvalidaException;
import br.com.gastrohub.exception.UsuarioNaoEncontradoException;
import br.com.gastrohub.repository.UsuarioRepository;
import br.com.gastrohub.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private static final String ROLE_ADMIN = "ROLE_" + TipoUsuarioEnum.ADMIN.name();

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorDeSenha;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UsuarioResponse criarUsuario(CriarUsuarioRequest request) {
        log.info("Criando usuário com login '{}' e tipo '{}'", request.login(), request.tipo());
        if (request.tipo() == TipoUsuarioEnum.ADMIN) {
            throw new OperacaoNaoPermitidaException("Não é permitido criar usuários do tipo ADMIN");
        }
        validarLoginUnico(request.login());
        validarEmailUnico(request.email());

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .login(request.login())
                .senha(codificadorDeSenha.encode(request.senha()))
                .tipo(request.tipo())
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário '{}' criado com sucesso. ID gerado: {}", usuarioSalvo.getLogin(), usuarioSalvo.getId());
        return converterParaResponse(usuarioSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse buscarUsuarioPorId(Long id) {
        Usuario usuario = buscarUsuario(id);
        return converterParaResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponse atualizarDadosDoUsuario(Long id, AtualizarUsuarioRequest request) {
        log.info("Atualizando dados do usuário ID {}", id);
        Usuario usuario = buscarUsuario(id);
        validarAcessoAlteracoes(usuario);
        aplicarAlteracoes(usuario, request);
        UsuarioResponse response = converterParaResponse(usuarioRepository.save(usuario));
        log.info("Usuário ID {} atualizado com sucesso", id);
        return response;
    }

    @Override
    @Transactional
    public void excluirUsuario(Long id) {
        Usuario usuario = buscarUsuario(id);
        validarAcessoAlteracoes(usuario);
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void trocarSenhaDoUsuario(Long id, TrocarSenhaRequest request) {
        log.info("Processando troca de senha para usuário ID {}", id);
        Usuario usuario = buscarUsuario(id);

        validarAcessoAlteracoes(usuario);

        String usuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean proprietarioDoLogin = usuario.getLogin().equals(usuarioLogado);

        if (proprietarioDoLogin && !codificadorDeSenha.matches(request.senhaAtual(), usuario.getSenha())) {
            log.warn("Tentativa de troca de senha com senha atual incorreta para usuário ID {}", id);
            throw new SenhaAtualInvalidaException();
        }

        usuario.setSenha(codificadorDeSenha.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
        log.info("Senha do usuário ID {} alterada com sucesso", id);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse validarLogin(ValidarLoginRequest request) {
        Usuario usuario = usuarioRepository.findByLogin(request.login())
                .orElseThrow(LoginOuSenhaInvalidosException::new);

        if (!codificadorDeSenha.matches(request.senha(), usuario.getSenha())) {
            throw new LoginOuSenhaInvalidosException();
        }

        String token = jwtService.gerarToken(usuario.getLogin());
        return new LoginResponse(token, "Bearer", jwtService.calcularDataExpiracao());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> buscarPorNome(String nome) {
        return usuarioRepository.buscarPorNomeFlexivel(nome).stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    private void aplicarAlteracoes(Usuario usuario, AtualizarUsuarioRequest request) {
        if (request.nome() != null) {
            usuario.setNome(request.nome());
        }
        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            validarEmailUnico(request.email());
            usuario.setEmail(request.email());
        }
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    private void validarLoginUnico(String login) {
        if (usuarioRepository.existsByLogin(login)) {
            log.warn("Tentativa de cadastro com login já existente: '{}'", login);
            throw new DadosJaCadastradosException("Login '" + login + "' já está em uso");
        }
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Tentativa de cadastro com email já existente: '{}'", email);
            throw new DadosJaCadastradosException("Email '" + email + "' já está em uso");
        }
    }

    private boolean admin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));
    }

    private void validarAcessoAlteracoes(Usuario usuario) {
        if (admin())
            return;
        String usuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!usuario.getLogin().equals(usuarioLogado)) {
            log.warn("Acesso negado: usuário '{}' tentou modificar dados de '{}'", usuarioLogado, usuario.getLogin());
            throw new AcessoNegadoException();
        }
    }

    private UsuarioResponse converterParaResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getLogin(),
                usuario.getTipo(),
                usuario.getDataUltimaAlteracao(),
                usuario.getDataCriacao());
    }
}
