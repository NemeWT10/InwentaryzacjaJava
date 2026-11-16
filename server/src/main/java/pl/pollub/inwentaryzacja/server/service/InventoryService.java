package pl.pollub.inwentaryzacja.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.EquipmentDto;
import pl.pollub.inwentaryzacja.server.dto.InventoryDto;
import pl.pollub.inwentaryzacja.server.dto.RoomDto;
import pl.pollub.inwentaryzacja.server.dto.WorkstationDto;
import pl.pollub.inwentaryzacja.server.model.Equipment;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.EquipmentRepository;
import pl.pollub.inwentaryzacja.server.repository.RoomRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final RoomRepository roomRepository;
    private final WorkstationRepository workstationRepository;
    private final EquipmentRepository equipmentRepository;

    public InventoryDto export() {
        return new InventoryDto(
                roomRepository.findAll().stream().map(RoomDto::from).toList(),
                workstationRepository.findAll().stream().map(WorkstationDto::from).toList(),
                equipmentRepository.findAll().stream().map(EquipmentDto::from).toList());
    }

    /** Wszystko z pliku zapisuje jako nowe rekordy; id z pliku służą tylko do powiązań. */
    @Transactional
    public void importInventory(InventoryDto inventory) {
        Map<Long, Room> rooms = new HashMap<>();
        for (RoomDto dto : inventory.rooms()) {
            Room room = new Room(null, dto.number(), dto.description());
            rooms.put(dto.id(), roomRepository.save(room));
        }

        Map<Long, Workstation> workstations = new HashMap<>();
        for (WorkstationDto dto : inventory.workstations()) {
            Room room = findImported(rooms, dto.roomId(), "sali");
            Workstation workstation = new Workstation(null, room, dto.number(), dto.notes());
            workstations.put(dto.id(), workstationRepository.save(workstation));
        }

        for (EquipmentDto dto : inventory.equipment()) {
            Workstation workstation = findImported(workstations, dto.workstationId(), "stanowiska");
            equipmentRepository.save(new Equipment(null, workstation, dto.type(), dto.name(),
                    dto.microcontroller(), dto.status()));
        }
    }

    private static <T> T findImported(Map<Long, T> imported, Long id, String name) {
        T found = imported.get(id);
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "W pliku nie ma " + name + " o id " + id);
        }
        return found;
    }
}
