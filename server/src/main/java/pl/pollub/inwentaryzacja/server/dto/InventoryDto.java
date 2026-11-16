package pl.pollub.inwentaryzacja.server.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record InventoryDto(@NotNull @Valid List<RoomDto> rooms,
                           @NotNull @Valid List<WorkstationDto> workstations,
                           @NotNull @Valid List<EquipmentDto> equipment) {
}
