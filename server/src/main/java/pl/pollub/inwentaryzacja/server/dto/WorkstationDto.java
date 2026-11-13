package pl.pollub.inwentaryzacja.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.pollub.inwentaryzacja.server.model.Workstation;

public record WorkstationDto(Long id, @NotNull Long roomId, @NotBlank String number, String notes) {

    public static WorkstationDto from(Workstation workstation) {
        return new WorkstationDto(workstation.getId(), workstation.getRoom().getId(),
                workstation.getNumber(), workstation.getNotes());
    }
}
