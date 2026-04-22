package br.com.gastrohub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resposta do login com token JWT")
public record LoginResponse(

        @Schema(description = "Token JWT para autenticação nas próximas requisições")
        String token,

        @Schema(description = "Tipo do token", example = "Bearer")
        String tipo,

        @Schema(description = "Data e hora de expiração do token")
        LocalDateTime expiracao

) {}
