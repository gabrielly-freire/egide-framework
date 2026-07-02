package br.imd.ufrn.config;

import br.imd.ufrn.user.AppUser;
import br.imd.ufrn.user.AppUserRepository;
import br.imd.ufrn.user.Role;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria um usuário administrador inicial se ainda não houver nenhum usuário, para permitir o primeiro
 * login. Idempotente: não faz nada se já existirem usuários. Credenciais configuráveis por
 * {@code app.admin.*}; a senha é codificada com BCrypt (nunca gravada em texto plano).
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        AppUser admin = new AppUser();
        admin.setName("Administrador");
        admin.setEmail("admin@egide.local");
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        log.info("Usuário admin inicial criado (username='{}').", adminUsername);
    }
}
