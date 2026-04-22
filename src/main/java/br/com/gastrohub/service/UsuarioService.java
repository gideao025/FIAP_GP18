package br.com.gastrohub.service;

import br.com.gastrohub.dto.request.*;
import br.com.gastrohub.dto.response.LoginResponse;
import br.com.gastrohub.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse criarUsuario(CriarUsuarioRequest request);

    UsuarioResponse buscarUsuarioPorId(Long id);

    List<UsuarioResponse> listarTodosUsuarios();

    UsuarioResponse atualizarDadosDoUsuario(Long id, AtualizarUsuarioRequest request);

    void excluirUsuario(Long id);

    void trocarSenhaDoUsuario(Long id, TrocarSenhaRequest request);

    LoginResponse validarLogin(ValidarLoginRequest request);

    List<UsuarioResponse> buscarPorNome(String nome);
}
