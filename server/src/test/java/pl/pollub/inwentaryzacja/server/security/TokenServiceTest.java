package pl.pollub.inwentaryzacja.server.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import pl.pollub.inwentaryzacja.server.config.SecurityConfig;
import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.User;

import javax.crypto.SecretKey;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenServiceTest {

    private static final SecretKey KEY = SecurityConfig.secretKey("klucz-testowy-co-najmniej-32-bajty!!");

    private final TokenService tokenService = new TokenService(
            NimbusJwtEncoder.withSecretKey(KEY).algorithm(MacAlgorithm.HS256).build(), 8);

    @Test
    void tokenContainsLoginRoleAndExpiry() {
        String token = tokenService.issue(new User(1L, "admin", "hash", Role.ADMIN));

        Jwt jwt = decoder(KEY).decode(token);

        assertEquals("admin", jwt.getSubject());
        assertEquals("ADMIN", jwt.getClaimAsString(SecurityConfig.ROLE_CLAIM));
        assertEquals(Duration.ofHours(8), Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()));
    }

    @Test
    void tokenSignedWithOtherKeyIsRejected() {
        String token = tokenService.issue(new User(1L, "admin", "hash", Role.ADMIN));
        SecretKey otherKey = SecurityConfig.secretKey("zupelnie-inny-klucz-tez-32-bajty!!!!");

        assertThrows(JwtException.class, () -> decoder(otherKey).decode(token));
    }

    @Test
    void tooShortSecretIsRejected() {
        assertThrows(IllegalStateException.class, () -> SecurityConfig.secretKey("krotki"));
    }

    private static JwtDecoder decoder(SecretKey key) {
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
