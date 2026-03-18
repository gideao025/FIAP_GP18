package org.gideao.pocuser.service;

import jakarta.transaction.Transactional;
import org.gideao.pocuser.domain.model.Usuario;
import org.gideao.pocuser.dto.LoginRequestDto;
import org.gideao.pocuser.dto.UsuarioRequestDto;
import org.gideao.pocuser.exception.CredenciaisInvalidasException;
import org.gideao.pocuser.exception.EmailJaCadastradoException;
import org.gideao.pocuser.exception.LoginJaCadastradoException;
import org.gideao.pocuser.exception.UsuarioNaoEncontradoException;
import org.gideao.pocuser.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario criar(UsuarioRequestDto requestDto) {
        validarUnicidadeNoCadastro(requestDto.email(), requestDto.login());

        Usuario usuario = new Usuario();
        usuario.setNome(requestDto.nome());
        usuario.setEmail(requestDto.email());
        usuario.setLogin(requestDto.login());
        usuario.setSenha(passwordEncoder.encode(requestDto.senha()));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, UsuarioRequestDto requestDto) {
        Usuario usuario = buscarPorId(id);
        validarUnicidadeNaAtualizacao(id, requestDto.email(), requestDto.login());

        usuario.setNome(requestDto.nome());
        usuario.setEmail(requestDto.email());
        usuario.setLogin(requestDto.login());
        usuario.setSenha(passwordEncoder.encode(requestDto.senha()));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    public void validarLogin(LoginRequestDto requestDto) {
        Usuario usuario = usuarioRepository.findByLogin(requestDto.login())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(requestDto.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    private void validarUnicidadeNoCadastro(String email, String login) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }
        if (usuarioRepository.existsByLogin(login)) {
            throw new LoginJaCadastradoException(login);
        }
    }

    private void validarUnicidadeNaAtualizacao(Long id, String email, String login) {
        if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new EmailJaCadastradoException(email);
        }
        if (usuarioRepository.existsByLoginAndIdNot(login, id)) {
            throw new LoginJaCadastradoException(login);
        }
    }
}
