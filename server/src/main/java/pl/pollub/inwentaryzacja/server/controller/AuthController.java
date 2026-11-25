package pl.pollub.inwentaryzacja.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.inwentaryzacja.server.dto.LoginRequest;
import pl.pollub.inwentaryzacja.server.dto.LoginResponse;
import pl.pollub.inwentaryzacja.server.service.AuthService;

@Tag(name = "Logowanie", description = "Logowanie i wystawianie tokenu JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login {"login": "...", "password": "..."} -> {"token": "...", "role": "LECTURER"}
    @Operation(summary = "Logowanie: zwraca token JWT i rolę, błędne dane 401")
    @SecurityRequirements
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
