package pl.pollub.inwentaryzacja.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.WorkstationDto;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.RoomRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkstationService {

    private final WorkstationRepository workstationRepository;
    private final RoomRepository roomRepository;

    public List<WorkstationDto> find(Long roomId) {
        List<Workstation> workstations = roomId == null
                ? workstationRepository.findAll()
                : workstationRepository.findByRoomId(roomId);
        return workstations.stream().map(WorkstationDto::from).toList();
    }

    @Transactional
    public WorkstationDto create(WorkstationDto dto) {
        Workstation workstation = new Workstation();
        apply(workstation, dto);
        return WorkstationDto.from(workstationRepository.save(workstation));
    }

    @Transactional
    public WorkstationDto update(Long id, WorkstationDto dto) {
        Workstation workstation = workstationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma stanowiska o id " + id));
        apply(workstation, dto);
        return WorkstationDto.from(workstation);
    }

    private void apply(Workstation workstation, WorkstationDto dto) {
        workstation.setRoom(findRoom(dto.roomId()));
        workstation.setNumber(dto.number());
        workstation.setNotes(dto.notes());
    }

    private Room findRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nie ma sali o id " + roomId));
    }
}
