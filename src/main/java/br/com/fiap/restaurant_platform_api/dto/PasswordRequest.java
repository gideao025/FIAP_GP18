package br.com.fiap.restaurant_platform_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for updating user password")
public class PasswordRequest {

    @Schema(description = "New password", example = "999999")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}