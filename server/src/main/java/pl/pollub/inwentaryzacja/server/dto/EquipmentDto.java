package pl.pollub.inwentaryzacja.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.pollub.inwentaryzacja.server.model.Equipment;
import pl.pollub.inwentaryzacja.server.model.EquipmentStatus;
import pl.pollub.inwentaryzacja.server.model.EquipmentType;

public record EquipmentDto(Long id, @NotNull Long workstationId, @NotNull EquipmentType type,
                           @NotBlank String name, String microcontroller, @NotNull EquipmentStatus status) {

    public static EquipmentDto from(Equipment equipment) {
        return new EquipmentDto(equipment.getId(), equipment.getWorkstation().getId(), equipment.getType(),
                equipment.getName(), equipment.getMicrocontroller(), equipment.getStatus());
    }
}
