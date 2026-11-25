package pl.pollub.inwentaryzacja.server.dto;

import jakarta.validation.constraints.NotNull;
import pl.pollub.inwentaryzacja.server.model.FaultReportStatus;

public record FaultReportStatusChange(@NotNull FaultReportStatus status) {
}
