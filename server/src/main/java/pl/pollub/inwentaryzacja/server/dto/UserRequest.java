package pl.pollub.inwentaryzacja.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.pollub.inwentaryzacja.server.model.Role;

/**
 * Konto zakładane lub edytowane przez administratora.
 * Hasło jest wymagane przy tworzeniu; przy edycji null oznacza „bez zmiany hasła”.
 */
public record UserRequest(@NotBlank @Size(max = 50) String login,
                          @Size(min = 6, max = 72) String password,
                          @NotNull Role role) {
}
