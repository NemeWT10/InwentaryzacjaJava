package pl.pollub.inwentaryzacja.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.inwentaryzacja.server.dto.UserDto;
import pl.pollub.inwentaryzacja.server.dto.UserRequest;
import pl.pollub.inwentaryzacja.server.service.UserService;

import java.util.List;

@Tag(name = "Użytkownicy", description = "Konta i role (administrator)")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Lista kont bez haseł (ADMIN)")
    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    // POST /api/users {"login": "...", "password": "...", "role": "STUDENT"}
    @Operation(summary = "Założenie konta (ADMIN)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserRequest user) {
        return userService.create(user);
    }

    // PUT /api/users/{id} - zmiana loginu i roli, haslo tylko gdy podane
    @Operation(summary = "Zmiana loginu, roli lub hasła (ADMIN)")
    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserRequest user) {
        return userService.update(id, user);
    }

    @Operation(summary = "Usunięcie konta bez zgłoszeń, nie własnego (ADMIN)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        userService.delete(id, authentication.getName());
    }
}
