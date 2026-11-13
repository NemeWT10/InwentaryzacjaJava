package pl.pollub.inwentaryzacja.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.LoginRequest;
import pl.pollub.inwentaryzacja.server.dto.LoginResponse;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;
import pl.pollub.inwentaryzacja.server.security.TokenService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /** Ten sam błąd 401 dla złego loginu i złego hasła, żeby nie zdradzać, które konta istnieją. */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.login())
                .filter(found -> passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Błędny login lub hasło"));
        return new LoginResponse(tokenService.issue(user), user.getRole());
    }
}
