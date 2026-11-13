package pl.pollub.inwentaryzacja.client.api;

public record FaultReportRequest(Long workstationId, String description) {
}
