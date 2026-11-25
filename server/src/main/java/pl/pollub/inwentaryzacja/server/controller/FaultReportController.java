package pl.pollub.inwentaryzacja.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.inwentaryzacja.server.dto.FaultReportDto;
import pl.pollub.inwentaryzacja.server.dto.FaultReportRequest;
import pl.pollub.inwentaryzacja.server.dto.FaultReportStatusChange;
import pl.pollub.inwentaryzacja.server.model.FaultReportStatus;
import pl.pollub.inwentaryzacja.server.service.FaultReportService;

import java.util.List;

@Tag(name = "Zgłoszenia usterek", description = "Zgłaszanie usterek przez studentów i obsługa przez wykładowców")
@RestController
@RequestMapping("/api/fault-reports")
@RequiredArgsConstructor
public class FaultReportController {

    private final FaultReportService faultReportService;

    // POST /api/fault-reports {"workstationId": 1, "description": "..."}, zglaszajacy = login z tokenu
    @Operation(summary = "Zgłoszenie usterki stanowiska; zgłaszający z tokenu (STUDENT)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FaultReportDto create(@Valid @RequestBody FaultReportRequest request, Authentication authentication) {
        return faultReportService.create(request, authentication.getName());
    }

    // GET /api/fault-reports?status=OPEN
    @Operation(summary = "Lista zgłoszeń, opcjonalnie wg statusu (LECTURER)")
    @GetMapping
    public List<FaultReportDto> find(@RequestParam(required = false) FaultReportStatus status) {
        return faultReportService.find(status);
    }

    // PUT /api/fault-reports/{id} {"status": "CLOSED"}
    @Operation(summary = "Zmiana statusu zgłoszenia, np. zamknięcie (LECTURER)")
    @PutMapping("/{id}")
    public FaultReportDto changeStatus(@PathVariable Long id, @Valid @RequestBody FaultReportStatusChange change) {
        return faultReportService.changeStatus(id, change.status());
    }
}
