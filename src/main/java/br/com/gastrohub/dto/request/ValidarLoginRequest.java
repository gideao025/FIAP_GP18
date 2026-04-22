package br.com.gastrohub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para validação de login")
public record ValidarLoginRequest(

        @NotBlank(message = "Login é obrigatório")
        @Schema(description = "Login do usuário", example = "rrodriguez")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        @Schema(description = "Senha do usuário", example = "senha123")
        String senha

) {}
