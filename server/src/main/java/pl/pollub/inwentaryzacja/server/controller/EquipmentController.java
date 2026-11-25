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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.inwentaryzacja.server.dto.EquipmentDto;
import pl.pollub.inwentaryzacja.server.service.EquipmentService;

import java.util.List;

@Tag(name = "Sprzęt", description = "Płytki, kable, programatory i moduły na stanowiskach")
@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    // GET /api/equipment?workstationId=1, GET /api/equipment?microcontroller=stm32
    @Operation(summary = "Lista sprzętu, filtry: stanowisko i fragment nazwy mikrokontrolera (każdy zalogowany)")
    @GetMapping
    public List<EquipmentDto> find(@RequestParam(required = false) Long workstationId,
                                   @RequestParam(required = false) String microcontroller) {
        return equipmentService.find(workstationId, microcontroller);
    }

    @Operation(summary = "Dodanie sprzętu (LECTURER)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentDto create(@Valid @RequestBody EquipmentDto equipment) {
        return equipmentService.create(equipment);
    }

    @Operation(summary = "Edycja sprzętu, np. zmiana stanu (LECTURER)")
    @PutMapping("/{id}")
    public EquipmentDto update(@PathVariable Long id, @Valid @RequestBody EquipmentDto equipment) {
        return equipmentService.update(id, equipment);
    }

    @Operation(summary = "Usunięcie sprzętu (LECTURER)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        equipmentService.delete(id);
    }
}
