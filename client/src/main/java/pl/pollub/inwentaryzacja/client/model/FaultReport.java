package pl.pollub.inwentaryzacja.client.model;

public record FaultReport(Long id, String roomNumber, String workstationNumber,
                          String reporterLogin, String description) {

    @Override
    public String toString() {
        return "Sala " + roomNumber + ", stanowisko " + workstationNumber + ": " + description
                + " (zgłosił: " + reporterLogin + ")";
    }
}
