package pl.pollub.inwentaryzacja.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.UserDto;
import pl.pollub.inwentaryzacja.server.dto.UserRequest;
import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.repository.FaultReportRepository;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FaultReportRepository faultReportRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    @Transactional
    public UserDto create(UserRequest request) {
        if (request.password() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nowe konto musi mieć hasło");
        }
        String login = request.login().strip();
        if (userRepository.existsByLogin(login)) {
            throw loginTaken(login);
        }
        User user = new User(null, login, passwordEncoder.encode(request.password()), request.role());
        return UserDto.from(userRepository.save(user));
    }

    /** Zmienia login i rolę; hasło tylko wtedy, gdy podano nowe. */
    @Transactional
    public UserDto update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma konta o id " + id));
        String login = request.login().strip();
        if (!login.equals(user.getLogin()) && userRepository.existsByLogin(login)) {
            throw loginTaken(login);
        }
        if (user.getRole() == Role.ADMIN && request.role() != Role.ADMIN
                && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Nie można odebrać roli ostatniemu administratorowi");
        }
        user.setLogin(login);
        user.setRole(request.role());
        if (request.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return UserDto.from(user);
    }

    /** Usuwa konto; nie własne i nie ostatniego administratora. */
    @Transactional
    public void delete(Long id, String currentLogin) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma konta o id " + id));
        // login z tokenu mógł się zmienić - wtedy trzeba zalogować się ponownie
        User current = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Zaloguj się ponownie"));
        if (current.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nie można usunąć własnego konta");
        }
        if (user.getRole() == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nie można usunąć ostatniego administratora");
        }
        if (faultReportRepository.existsByReporterId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Konto " + user.getLogin() + " ma zgłoszenia usterek, nie można go usunąć");
        }
        userRepository.delete(user);
    }

    private static ResponseStatusException loginTaken(String login) {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Login " + login + " jest już zajęty");
    }
}
