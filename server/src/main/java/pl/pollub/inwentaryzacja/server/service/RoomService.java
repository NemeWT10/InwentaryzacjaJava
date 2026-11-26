package pl.pollub.inwentaryzacja.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.RoomDto;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.repository.RoomRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final WorkstationRepository workstationRepository;

    public List<RoomDto> findAll() {
        return roomRepository.findAll().stream().map(RoomDto::from).toList();
    }

    @Transactional
    public RoomDto create(RoomDto dto) {
        if (roomRepository.existsByNumber(dto.number())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala " + dto.number() + " już istnieje");
        }
        Room room = new Room(null, dto.number(), dto.description());
        return RoomDto.from(roomRepository.save(room));
    }

    /** Usuwa tylko pustą salę, bez stanowisk. */
    @Transactional
    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma sali o id " + id);
        }
        if (workstationRepository.existsByRoomId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala ma stanowiska, nie można jej usunąć");
        }
        roomRepository.deleteById(id);
    }
}
