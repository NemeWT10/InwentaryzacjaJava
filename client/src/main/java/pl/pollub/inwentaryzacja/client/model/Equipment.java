package pl.pollub.inwentaryzacja.client.model;

public record Equipment(Long id, Long workstationId, EquipmentType type, String name,
                        String microcontroller, EquipmentStatus status) {
}
