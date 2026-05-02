package br.com.gastrohub.controller;

import br.com.gastrohub.controller.api.UsuarioApi;
import br.com.gastrohub.dto.request.*;
import br.com.gastrohub.dto.response.LoginResponse;
import br.com.gastrohub.dto.response.UsuarioResponse;
import br.com.gastrohub.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController implements UsuarioApi {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        UsuarioResponse response = usuarioService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable Long id) {
        UsuarioResponse response = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodosUsuarios() {
        List<UsuarioResponse> response = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioResponse>> buscarPorNome(
            @RequestParam(name = "nome") String nome) {
        List<UsuarioResponse> response = usuarioService.buscarPorNome(nome);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarDadosDoUsuario(
            @PathVariable Long id, @Valid @RequestBody AtualizarUsuarioRequest request) {
        UsuarioResponse response = usuarioService.atualizarDadosDoUsuario(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        usuarioService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> trocarSenhaDoUsuario(
            @PathVariable Long id,
            @Valid @RequestBody TrocarSenhaRequest request) {
        usuarioService.trocarSenhaDoUsuario(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> validarLogin(@Valid @RequestBody ValidarLoginRequest request) {
        LoginResponse response = usuarioService.validarLogin(request);
        return ResponseEntity.ok(response);
    }
}
