package pl.pollub.inwentaryzacja.server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.FaultReportDto;
import pl.pollub.inwentaryzacja.server.dto.FaultReportRequest;
import pl.pollub.inwentaryzacja.server.model.FaultReport;
import pl.pollub.inwentaryzacja.server.model.FaultReportStatus;
import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.FaultReportRepository;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaultReportServiceTest {

    @Mock
    private FaultReportRepository faultReportRepository;

    @Mock
    private WorkstationRepository workstationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FaultReportService faultReportService;

    private final User student = new User(3L, "student", "hash", Role.STUDENT);
    private final Workstation workstation = new Workstation(1L, new Room(1L, "E203", null), "4", null);

    @Test
    void createSetsReporterFromTokenAndOpenStatus() {
        when(userRepository.findByLogin("student")).thenReturn(Optional.of(student));
        when(workstationRepository.findById(1L)).thenReturn(Optional.of(workstation));
        when(faultReportRepository.save(any(FaultReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FaultReportDto result = faultReportService.create(
                new FaultReportRequest(1L, "  Nie działa port USB "), "student");

        assertEquals("student", result.reporterLogin());
        assertEquals(FaultReportStatus.OPEN, result.status());
        assertEquals("E203", result.roomNumber());
        assertEquals("4", result.workstationNumber());
        assertEquals("Nie działa port USB", result.description());
    }

    @Test
    void createForUnknownWorkstationReturns400() {
        when(userRepository.findByLogin("student")).thenReturn(Optional.of(student));
        when(workstationRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> faultReportService.create(new FaultReportRequest(99L, "Brak zasilacza"), "student"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(faultReportRepository, never()).save(any());
    }

    @Test
    void closingMissingReportReturns404() {
        when(faultReportRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> faultReportService.changeStatus(5L, FaultReportStatus.CLOSED));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void closingReportChangesItsStatus() {
        FaultReport report = new FaultReport(5L, workstation, student, "Uszkodzony kabel",
                FaultReportStatus.OPEN, null);
        when(faultReportRepository.findById(5L)).thenReturn(Optional.of(report));

        FaultReportDto result = faultReportService.changeStatus(5L, FaultReportStatus.CLOSED);

        assertEquals(FaultReportStatus.CLOSED, result.status());
        assertEquals(FaultReportStatus.CLOSED, report.getStatus());
    }

    @Test
    void withoutStatusAllReportsAreReturned() {
        when(faultReportRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        faultReportService.find(null);

        verify(faultReportRepository, never()).findByStatusOrderByCreatedAtDesc(any());
    }
}
