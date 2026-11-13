package pl.pollub.inwentaryzacja.server.dto;

import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.User;

/** Użytkownik wysyłany do klienta, celowo bez passwordHash. */
public record UserDto(Long id, String login, Role role) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getLogin(), user.getRole());
    }
}
