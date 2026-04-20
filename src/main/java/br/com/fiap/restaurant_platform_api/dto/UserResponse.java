package br.com.fiap.restaurant_platform_api.dto;

import br.com.fiap.restaurant_platform_api.entity.User;
import br.com.fiap.restaurant_platform_api.entity.UserType;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String login,
        String address,
        UserType userType,
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