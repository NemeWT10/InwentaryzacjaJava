package pl.pollub.inwentaryzacja.server.controller;

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

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    // GET /api/equipment?workstationId=1, GET /api/equipment?microcontroller=stm32
    @GetMapping
    public List<EquipmentDto> find(@RequestParam(required = false) Long workstationId,
                                   @RequestParam(required = false) String microcontroller) {
        return equipmentService.find(workstationId, microcontroller);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentDto create(@Valid @RequestBody EquipmentDto equipment) {
        return equipmentService.create(equipment);
    }

    @PutMapping("/{id}")
    public EquipmentDto update(@PathVariable Long id, @Valid @RequestBody EquipmentDto equipment) {
        return equipmentService.update(id, equipment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        equipmentService.delete(id);
    }
}
