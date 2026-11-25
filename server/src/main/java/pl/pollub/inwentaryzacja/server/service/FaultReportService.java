package pl.pollub.inwentaryzacja.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.FaultReportDto;
import pl.pollub.inwentaryzacja.server.dto.FaultReportRequest;
import pl.pollub.inwentaryzacja.server.model.FaultReport;
import pl.pollub.inwentaryzacja.server.model.FaultReportStatus;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.FaultReportRepository;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaultReportService {

    private final FaultReportRepository faultReportRepository;
    private final WorkstationRepository workstationRepository;
    private final UserRepository userRepository;

    /** Najnowsze zgłoszenia na górze; bez statusu zwraca wszystkie. */
    public List<FaultReportDto> find(FaultReportStatus status) {
        List<FaultReport> reports = status == null
                ? faultReportRepository.findAllByOrderByCreatedAtDesc()
                : faultReportRepository.findByStatusOrderByCreatedAtDesc(status);
        return reports.stream().map(FaultReportDto::from).toList();
    }

    @Transactional
    public FaultReportDto create(FaultReportRequest request, String reporterLogin) {
        User reporter = userRepository.findByLogin(reporterLogin)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Konto z tokenu już nie istnieje"));
        Workstation workstation = workstationRepository.findById(request.workstationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Nie ma stanowiska o id " + request.workstationId()));

        FaultReport report = new FaultReport();
        report.setWorkstation(workstation);
        report.setReporter(reporter);
        report.setDescription(request.description().strip());
        report.setStatus(FaultReportStatus.OPEN);
        return FaultReportDto.from(faultReportRepository.save(report));
    }

    @Transactional
    public FaultReportDto changeStatus(Long id, FaultReportStatus status) {
        FaultReport report = faultReportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nie ma zgłoszenia o id " + id));
        report.setStatus(status);
        return FaultReportDto.from(report);
    }
}
