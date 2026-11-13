package pl.pollub.inwentaryzacja.server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.WorkstationDto;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.RoomRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkstationServiceTest {

    @Mock
    private WorkstationRepository workstationRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private WorkstationService workstationService;

    @Test
    void updateMissingWorkstationReturnsNotFound() {
        when(workstationRepository.findById(7L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> workstationService.update(7L, new WorkstationDto(null, 1L, "3", null)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateMovesWorkstationToAnotherRoom() {
        Room oldRoom = new Room(1L, "E203", null);
        Room newRoom = new Room(2L, "E204", null);
        Workstation workstation = new Workstation(7L, oldRoom, "3", null);
        when(workstationRepository.findById(7L)).thenReturn(Optional.of(workstation));
        when(roomRepository.findById(2L)).thenReturn(Optional.of(newRoom));

        WorkstationDto result = workstationService.update(7L, new WorkstationDto(null, 2L, "3", "Nowy zasilacz"));

        assertEquals(7L, result.id());
        assertEquals(2L, result.roomId());
        assertEquals("Nowy zasilacz", result.notes());
    }
}
