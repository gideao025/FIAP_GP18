package br.com.gastrohub.config;

import br.com.gastrohub.entity.Usuario;
import br.com.gastrohub.enums.TipoUsuarioEnum;
import br.com.gastrohub.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorDeSenha;

    @Override
    public void run(ApplicationArguments args) {
        if (!usuarioRepository.existsByLogin("admin")) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador")
                    .email("admin@gastrohub.com")
                    .login("admin")
                    .senha(codificadorDeSenha.encode("admin123"))
                    .tipo(TipoUsuarioEnum.ADMIN)
                    .build();
            usuarioRepository.save(admin);
        }
    }
}
