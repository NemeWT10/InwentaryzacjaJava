package pl.pollub.inwentaryzacja.client.model;

public record Workstation(Long id, Long roomId,
                          String number, String notes) {

    @Override
    public String toString() {
        return "Stanowisko " + number;
    }
}
