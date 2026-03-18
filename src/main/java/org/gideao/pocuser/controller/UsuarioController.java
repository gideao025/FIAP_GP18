package org.gideao.pocuser.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.gideao.pocuser.domain.model.Usuario;
import org.gideao.pocuser.dto.LoginRequestDto;
import org.gideao.pocuser.dto.LoginResponseDto;
import org.gideao.pocuser.dto.UsuarioRequestDto;
import org.gideao.pocuser.dto.UsuarioResponseDto;
import org.gideao.pocuser.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> criar(@Valid @RequestBody UsuarioRequestDto requestDto) {
        Usuario usuario = usuarioService.criar(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity.created(location).body(toResponse(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto requestDto
    ) {
        Usuario usuario = usuarioService.atualizar(id, requestDto);
        return ResponseEntity.ok(toResponse(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        usuarioService.validarLogin(requestDto);
        return ResponseEntity.ok(new LoginResponseDto("Login realizado com sucesso"));
    }

    private UsuarioResponseDto toResponse(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getLogin()
        );
    }
}
