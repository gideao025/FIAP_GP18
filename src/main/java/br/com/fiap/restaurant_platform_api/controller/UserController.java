package br.com.fiap.restaurant_platform_api.controller;

import br.com.fiap.restaurant_platform_api.dto.LoginRequest;
import br.com.fiap.restaurant_platform_api.dto.PasswordRequest;
import br.com.fiap.restaurant_platform_api.dto.UserResponse;
import br.com.fiap.restaurant_platform_api.entity.User;
import br.com.fiap.restaurant_platform_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Email already exists")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody User user) {

        User createdUser = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.from(createdUser));
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
                        .stream()
                        .map(UserResponse::from)
                        .toList()
        );
    }

    @Operation(summary = "Update user data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search users by name")
    @ApiResponse(responseCode = "200", description = "Users found")
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(
                userService.searchUsersByName(name)
                        .stream()
                        .map(UserResponse::from)
                        .toList()
        );
    }

    @Operation(summary = "Update user password")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id,
                                               @RequestBody PasswordRequest request) {
        userService.updatePassword(id, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Validate user login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login valid"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                userService.validateLogin(request.getLogin(), request.getPassword())
        );
    }
}