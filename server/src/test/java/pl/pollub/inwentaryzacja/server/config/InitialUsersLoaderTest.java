package pl.pollub.inwentaryzacja.server.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitialUsersLoaderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InitialUsersLoader loader;

    @Test
    void emptyDatabaseGetsOneAccountPerRoleWithHashedPassword() {
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "hash-" + invocation.getArgument(0));

        loader.run(null);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(3)).save(saved.capture());
        List<User> users = saved.getAllValues();
        assertEquals(List.of(Role.STUDENT, Role.LECTURER, Role.ADMIN), users.stream().map(User::getRole).toList());
        assertTrue(users.stream().allMatch(user -> user.getPasswordHash().startsWith("hash-")));
    }

    @Test
    void existingUsersAreLeftUntouched() {
        when(userRepository.count()).thenReturn(5L);

        loader.run(null);

        verify(userRepository, never()).save(any());
    }
}
