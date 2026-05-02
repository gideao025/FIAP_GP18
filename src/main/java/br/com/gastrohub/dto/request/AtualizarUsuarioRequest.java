package br.com.gastrohub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização do usuário — todos os campos são opcionais")
public record AtualizarUsuarioRequest(

        @Size(min = 1, message = "Nome não pode ser vazio")
        @Schema(description = "Novo nome do usuário", example = "Kleber Pereira")
        String nome,

        @Email(message = "Email inválido")
        @Schema(description = "E-mail do usuário", example = "kleber.pereira@email.com")
        String email

) {}
