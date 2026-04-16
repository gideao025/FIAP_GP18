package br.com.fiap.restaurant_platform_api.repository;

import br.com.fiap.restaurant_platform_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByNameContainingIgnoreCase(String name);

    Optional<User> findByLoginAndPassword(String login, String password);

    boolean existsByEmail(String email);
}
