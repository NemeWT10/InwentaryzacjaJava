package pl.pollub.inwentaryzacja.server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.LoginRequest;
import pl.pollub.inwentaryzacja.server.dto.LoginResponse;
import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;
import pl.pollub.inwentaryzacja.server.security.TokenService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private final User lecturer = new User(1L, "wykladowca", "hash", Role.LECTURER);

    @Test
    void loginWithCorrectPasswordReturnsTokenAndRole() {
        when(userRepository.findByLogin("wykladowca")).thenReturn(Optional.of(lecturer));
        when(passwordEncoder.matches("wykladowca123", "hash")).thenReturn(true);
        when(tokenService.issue(lecturer)).thenReturn("token-jwt");

        LoginResponse response = authService.login(new LoginRequest("wykladowca", "wykladowca123"));

        assertEquals("token-jwt", response.token());
        assertEquals(Role.LECTURER, response.role());
    }

    @Test
    void loginWithWrongPasswordReturns401WithoutToken() {
        when(userRepository.findByLogin("wykladowca")).thenReturn(Optional.of(lecturer));
        when(passwordEncoder.matches("zle", "hash")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(new LoginRequest("wykladowca", "zle")));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(tokenService, never()).issue(any());
    }

    @Test
    void loginWithUnknownUserReturns401() {
        when(userRepository.findByLogin("nikt")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(new LoginRequest("nikt", "haslo")));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}
