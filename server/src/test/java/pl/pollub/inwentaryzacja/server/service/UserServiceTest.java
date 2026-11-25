package pl.pollub.inwentaryzacja.server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.UserDto;
import pl.pollub.inwentaryzacja.server.dto.UserRequest;
import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createStoresHashedPasswordAndReturnsUserWithoutIt() {
        when(userRepository.existsByLogin("kowalski")).thenReturn(false);
        when(passwordEncoder.encode("tajne123")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            assertEquals("hash", saved.getPasswordHash());
            saved.setId(4L);
            return saved;
        });

        UserDto result = userService.create(new UserRequest(" kowalski ", "tajne123", Role.LECTURER));

        assertEquals(new UserDto(4L, "kowalski", Role.LECTURER), result);
    }

    @Test
    void createWithTakenLoginReturns409() {
        when(userRepository.existsByLogin("student")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.create(new UserRequest("student", "tajne123", Role.STUDENT)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createWithoutPasswordReturns400() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.create(new UserRequest("kowalski", null, Role.STUDENT)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void updateWithoutPasswordKeepsOldHash() {
        User user = new User(4L, "kowalski", "stary-hash", Role.STUDENT);
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));

        UserDto result = userService.update(4L, new UserRequest("kowalski", null, Role.LECTURER));

        assertEquals(Role.LECTURER, result.role());
        assertEquals("stary-hash", user.getPasswordHash());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void lastAdminCannotLoseAdminRole() {
        User admin = new User(1L, "admin", "hash", Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.update(1L, new UserRequest("admin", null, Role.STUDENT)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(Role.ADMIN, admin.getRole());
    }
}
