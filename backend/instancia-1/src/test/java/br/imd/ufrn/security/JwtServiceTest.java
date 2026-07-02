package br.imd.ufrn.security;

import static org.assertj.core.api.Assertions.assertThat;

import br.imd.ufrn.user.AppUser;
import br.imd.ufrn.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret",
                "egide-test-secret-0123456789-0123456789-0123456789");
        ReflectionTestUtils.setField(jwtService, "expirationInMs", 86_400_000L);
    }

    private AppUser user() {
        AppUser user = new AppUser();
        user.setUsername("ana.souza");
        user.setRole(Role.LISTENER);
        return user;
    }

    @Test
    void generateToken_deveEmbutirUsername_recuperavelDoToken() {
        AppUser user = user();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("ana.souza");
    }

    @Test
    void isTokenValid_deveSerVerdadeiro_paraOMesmoUsuario() {
        AppUser user = user();
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }
}
