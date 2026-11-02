package pl.pollub.inwentaryzacja.client.api;

import pl.pollub.inwentaryzacja.client.model.Role;

public record LoginResponse(String token, Role role) {
}
