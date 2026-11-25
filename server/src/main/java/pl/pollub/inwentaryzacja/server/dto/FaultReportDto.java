package pl.pollub.inwentaryzacja.server.dto;

import pl.pollub.inwentaryzacja.server.model.FaultReport;
import pl.pollub.inwentaryzacja.server.model.FaultReportStatus;
import pl.pollub.inwentaryzacja.server.model.Workstation;

import java.time.Instant;

/** Zgłoszenie z numerami sali i stanowiska oraz loginem zgłaszającego, do wyświetlenia na liście. */
public record FaultReportDto(Long id, Long workstationId, String roomNumber, String workstationNumber,
                             String reporterLogin, String description, FaultReportStatus status,
                             Instant createdAt) {

    public static FaultReportDto from(FaultReport report) {
        Workstation workstation = report.getWorkstation();
        return new FaultReportDto(report.getId(), workstation.getId(), workstation.getRoom().getNumber(),
                workstation.getNumber(), report.getReporter().getLogin(), report.getDescription(),
                report.getStatus(), report.getCreatedAt());
    }
}
