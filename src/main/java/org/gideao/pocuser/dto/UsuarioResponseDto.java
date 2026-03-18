package org.gideao.pocuser.dto;

public record UsuarioResponseDto(
        Long id,
        String nome,
        String email,
        String login
) {
}
