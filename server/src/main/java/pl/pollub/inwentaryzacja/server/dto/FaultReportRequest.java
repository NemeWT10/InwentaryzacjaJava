package pl.pollub.inwentaryzacja.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Zgłoszenie od studenta: zgłaszającego, status i datę ustawia serwer. */
public record FaultReportRequest(@NotNull Long workstationId, @NotBlank @Size(max = 255) String description) {
}
