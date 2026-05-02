package br.com.gastrohub.dto.request;

import br.com.gastrohub.enums.TipoUsuarioEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para criar um novo usuário")
public record CriarUsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Schema(description = "Nome completo do usuário", example = "Roberto Rodriguez")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Schema(description = "Endereço de e-mail do usuário", example = "roberto.rodriguez@email.com")
        String email,

        @NotBlank(message = "Login é obrigatório")
        @Size(min = 3, max = 50, message = "Login deve ter entre 3 e 50 caracteres")
        @Schema(description = "Login de acesso único no sistema", example = "rrodriguez")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "senha123")
        String senha,

        @NotNull(message = "Tipo de usuário é obrigatório")
        @Schema(description = "Tipo do usuário no sistema", example = "CLIENTE")
        TipoUsuarioEnum tipo

) {}
