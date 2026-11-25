package pl.pollub.inwentaryzacja.client.api;

import pl.pollub.inwentaryzacja.client.model.Role;

// password null = hasło bez zmian
public record UserRequest(String login, String password, Role role) {
}
