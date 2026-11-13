package pl.pollub.inwentaryzacja.server.dto;

import jakarta.validation.constraints.NotBlank;
import pl.pollub.inwentaryzacja.server.model.Room;

public record RoomDto(Long id, @NotBlank String number, String description) {

    public static RoomDto from(Room room) {
        return new RoomDto(room.getId(), room.getNumber(), room.getDescription());
    }
}
