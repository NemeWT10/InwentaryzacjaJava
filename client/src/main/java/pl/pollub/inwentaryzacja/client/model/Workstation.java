package pl.pollub.inwentaryzacja.client.model;

public record Workstation(Long id, Long roomId,
                          String number, String notes) {
}
