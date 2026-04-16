package br.com.fiap.restaurant_platform_api.service;

import br.com.fiap.restaurant_platform_api.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();
}
