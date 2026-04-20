package br.com.fiap.restaurant_platform_api.dto;

import br.com.fiap.restaurant_platform_api.entity.User;
import br.com.fiap.restaurant_platform_api.entity.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User response returned by the API")
public record UserResponse(

        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "User full name", example = "Victor Silva")
        String name,

        @Schema(description = "User email", example = "victor@email.com")
        String email,

        @Schema(description = "User login username", example = "victor")
        String login,

        @Schema(description = "User address", example = "Rua A, 100")
        String address,

        @Schema(description = "User type (CLIENT or OWNER)", example = "CLIENT")
        UserType userType,

        @Schema(description = "Last update timestamp", example = "2026-04-20T14:51:17")
        LocalDateTime lastUpdate
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getAddress(),
                user.getUserType(),
                user.getLastUpdate()
        );
    }
}