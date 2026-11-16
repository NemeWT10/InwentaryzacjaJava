package pl.pollub.inwentaryzacja.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.inwentaryzacja.server.dto.LoginRequest;
import pl.pollub.inwentaryzacja.server.dto.LoginResponse;
import pl.pollub.inwentaryzacja.server.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login {"login": "...", "password": "..."} -> {"token": "...", "role": "LECTURER"}
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
