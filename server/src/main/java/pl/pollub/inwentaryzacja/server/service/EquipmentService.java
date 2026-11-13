package pl.pollub.inwentaryzacja.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.EquipmentDto;
import pl.pollub.inwentaryzacja.server.model.Equipment;
import pl.pollub.inwentaryzacja.server.model.EquipmentType;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.EquipmentRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final WorkstationRepository workstationRepository;

    /** Filtry są opcjonalne i można je łączyć; mikrokontroler szukany jest jako fragment, bez wielkości liter. */
    public List<EquipmentDto> find(Long workstationId, String microcontroller) {
        String fragment = blankToNull(microcontroller);
        List<Equipment> equipment;
        if (workstationId != null && fragment != null) {
            equipment = equipmentRepository
                    .findByWorkstationIdAndMicrocontrollerContainingIgnoreCase(workstationId, fragment);
        } else if (workstationId != null) {
            equipment = equipmentRepository.findByWorkstationId(workstationId);
        } else if (fragment != null) {
            equipment = equipmentRepository.findByMicrocontrollerContainingIgnoreCase(fragment);
        } else {
            equipment = equipmentRepository.findAll();
        }
        return equipment.stream().map(EquipmentDto::from).toList();
    }

    @Transactional
    public EquipmentDto create(EquipmentDto dto) {
        Equipment equipment = new Equipment();
        apply(equipment, dto);
        return EquipmentDto.from(equipmentRepository.save(equipment));
    }

    @Transactional
    public EquipmentDto update(Long id, EquipmentDto dto) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> notFound(id));
        apply(equipment, dto);
        return EquipmentDto.from(equipment);
    }

    @Transactional
    public void delete(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw notFound(id);
        }
        equipmentRepository.deleteById(id);
    }

    private void apply(Equipment equipment, EquipmentDto dto) {
        String microcontroller = blankToNull(dto.microcontroller());
        if (microcontroller != null && dto.type() != EquipmentType.BOARD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mikrokontroler podaje się tylko dla płytek (BOARD)");
        }
        equipment.setWorkstation(findWorkstation(dto.workstationId()));
        equipment.setType(dto.type());
        equipment.setName(dto.name());
        equipment.setMicrocontroller(microcontroller);
        equipment.setStatus(dto.status());
    }

    private Workstation findWorkstation(Long workstationId) {
        return workstationRepository.findById(workstationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Nie ma stanowiska o id " + workstationId));
    }

    private static ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma sprzętu o id " + id);
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.strip();
    }
}
