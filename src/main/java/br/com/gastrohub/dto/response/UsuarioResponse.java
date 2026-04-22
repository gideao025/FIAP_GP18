package br.com.gastrohub.dto.response;

import br.com.gastrohub.enums.TipoUsuarioEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

// ATENÇÃO: campo senha NÃO está incluído propositalmente
@Schema(description = "Dados do usuário retornados pela API — campo senha nunca é incluído")
public record UsuarioResponse(

        @Schema(description = "ID único do usuário", example = "1")
        Long id,

        @Schema(description = "Nome completo do usuário", example = "Roberto Rodriguez")
        String nome,

        @Schema(description = "E-mail do usuário", example = "roberto.rodriguez@email.com")
        String email,

        @Schema(description = "Login de acesso do usuário", example = "rrodriguez")
        String login,

        @Schema(description = "Tipo do usuário no sistema", example = "CLIENTE")
        TipoUsuarioEnum tipo,

        @Schema(description = "Data e hora da última alteração dos dados")
        LocalDateTime dataUltimaAlteracao,

        @Schema(description = "Data e hora de criação do cadastro")
        LocalDateTime dataCriacao

) {}
