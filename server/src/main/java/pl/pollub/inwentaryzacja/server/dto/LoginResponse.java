package pl.pollub.inwentaryzacja.server.dto;

import pl.pollub.inwentaryzacja.server.model.Role;

public record LoginResponse(String token, Role role) {
}
