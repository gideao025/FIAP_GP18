package br.com.gastrohub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para troca de senha do usuário")
public record TrocarSenhaRequest(

        @NotBlank(message = "Senha atual é obrigatória")
        @Schema(description = "Senha atual do usuário para confirmação", example = "senhaAtual123")
        String senhaAtual,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
        @Schema(description = "Nova senha desejada (mínimo 6 caracteres)", example = "novaSenha456")
        String novaSenha

) {}
