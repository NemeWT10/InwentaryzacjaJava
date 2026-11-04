package pl.pollub.inwentaryzacja.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.inwentaryzacja.server.model.FaultReport;
import pl.pollub.inwentaryzacja.server.model.FaultReportStatus;

import java.util.List;

public interface FaultReportRepository extends JpaRepository<FaultReport, Long> {

    List<FaultReport> findByStatus(FaultReportStatus status);
}
