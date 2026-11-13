package pl.pollub.inwentaryzacja.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.pollub.inwentaryzacja.server.dto.RoomDto;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

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
}
