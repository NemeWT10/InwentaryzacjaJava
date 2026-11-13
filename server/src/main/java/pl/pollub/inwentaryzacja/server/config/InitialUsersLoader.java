package pl.pollub.inwentaryzacja.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;

/** Przy pustej tabeli users tworzy po jednym koncie na rolę, żeby było się na co zalogować. */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitialUsersLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return;
        }
        create("student", "student123", Role.STUDENT);
        create("wykladowca", "wykladowca123", Role.LECTURER);
        create("admin", "admin123", Role.ADMIN);
        log.info("Pusta baza: utworzono konta startowe student, wykladowca i admin");
    }

    private void create(String login, String password, Role role) {
        userRepository.save(new User(null, login, passwordEncoder.encode(password), role));
    }
}
