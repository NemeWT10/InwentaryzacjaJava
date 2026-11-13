package pl.pollub.inwentaryzacja.server.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import pl.pollub.inwentaryzacja.server.config.SecurityConfig;
import pl.pollub.inwentaryzacja.server.model.User;

import java.time.Duration;
import java.time.Instant;

/** Wystawia token JWT: login w "sub", rola w "role". */
@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final Duration validity;

    public TokenService(JwtEncoder jwtEncoder, @Value("${jwt.validity-hours}") long validityHours) {
        this.jwtEncoder = jwtEncoder;
        this.validity = Duration.ofHours(validityHours);
    }

    public String issue(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("inwentaryzacja")
                .subject(user.getLogin())
                .issuedAt(now)
                .expiresAt(now.plus(validity))
                .claim(SecurityConfig.ROLE_CLAIM, user.getRole().name())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
