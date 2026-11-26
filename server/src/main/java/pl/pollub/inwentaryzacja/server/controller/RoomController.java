package pl.pollub.inwentaryzacja.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.inwentaryzacja.server.dto.RoomDto;
import pl.pollub.inwentaryzacja.server.service.RoomService;

import java.util.List;

@Tag(name = "Sale", description = "Sale pracowni")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Lista sal (każdy zalogowany)")
    @GetMapping
    public List<RoomDto> findAll() {
        return roomService.findAll();
    }

    @Operation(summary = "Dodanie sali (ADMIN)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDto create(@Valid @RequestBody RoomDto room) {
        return roomService.create(room);
    }

    @Operation(summary = "Usunięcie pustej sali, bez stanowisk (ADMIN)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }
}
