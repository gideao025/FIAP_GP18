package br.com.fiap.restaurant_platform_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for user login")
public class LoginRequest {

    @Schema(description = "User login username", example = "victor")
    private String login;

    @Schema(description = "User password", example = "123456")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}