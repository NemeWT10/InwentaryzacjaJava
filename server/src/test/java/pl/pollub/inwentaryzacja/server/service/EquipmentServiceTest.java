package pl.pollub.inwentaryzacja.server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.EquipmentDto;
import pl.pollub.inwentaryzacja.server.model.Equipment;
import pl.pollub.inwentaryzacja.server.model.EquipmentStatus;
import pl.pollub.inwentaryzacja.server.model.EquipmentType;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.EquipmentRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private WorkstationRepository workstationRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private final Workstation workstation = new Workstation(1L, new Room(1L, "E203", null), "1", null);

    @Test
    void createReturnsDtoWithWorkstationIdInsteadOfObject() {
        when(workstationRepository.findById(1L)).thenReturn(Optional.of(workstation));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> {
            Equipment saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        EquipmentDto result = equipmentService.create(new EquipmentDto(null, 1L, EquipmentType.BOARD,
                "NUCLEO-L476RG", " STM32L476RG ", EquipmentStatus.WORKING));

        assertEquals(10L, result.id());
        assertEquals(1L, result.workstationId());
        assertEquals("STM32L476RG", result.microcontroller());
    }

    @Test
    void createRejectsMicrocontrollerForNonBoard() {
        EquipmentDto cable = new EquipmentDto(null, 1L, EquipmentType.CABLE, "USB-C", "STM32",
                EquipmentStatus.WORKING);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> equipmentService.create(cable));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(equipmentRepository, never()).save(any());
    }

    @Test
    void createRejectsUnknownWorkstation() {
        when(workstationRepository.findById(99L)).thenReturn(Optional.empty());
        EquipmentDto board = new EquipmentDto(null, 99L, EquipmentType.BOARD, "Arduino Uno", "ATmega328P",
                EquipmentStatus.WORKING);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> equipmentService.create(board));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createStoresBlankMicrocontrollerAsNull() {
        when(workstationRepository.findById(1L)).thenReturn(Optional.of(workstation));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EquipmentDto result = equipmentService.create(new EquipmentDto(null, 1L, EquipmentType.PROGRAMMER,
                "ST-LINK V2", "  ", EquipmentStatus.WORKING));

        assertNull(result.microcontroller());
    }

    @Test
    void findByMicrocontrollerTrimsFragmentAndIgnoresCase() {
        Equipment board = new Equipment(5L, workstation, EquipmentType.BOARD, "NUCLEO-L476RG", "STM32L476RG",
                EquipmentStatus.WORKING);
        when(equipmentRepository.findByMicrocontrollerContainingIgnoreCase("stm32")).thenReturn(List.of(board));

        List<EquipmentDto> result = equipmentService.find(null, " stm32 ");

        assertEquals(1, result.size());
        assertEquals("NUCLEO-L476RG", result.getFirst().name());
    }

    @Test
    void deleteMissingEquipmentReturnsNotFound() {
        when(equipmentRepository.existsById(42L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> equipmentService.delete(42L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(equipmentRepository, never()).deleteById(any());
    }
}
