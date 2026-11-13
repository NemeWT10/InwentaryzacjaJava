package pl.pollub.inwentaryzacja.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.inwentaryzacja.server.dto.WorkstationDto;
import pl.pollub.inwentaryzacja.server.service.WorkstationService;

import java.util.List;

@RestController
@RequestMapping("/api/workstations")
@RequiredArgsConstructor
public class WorkstationController {

    private final WorkstationService workstationService;

    // GET /api/workstations?roomId=1
    @GetMapping
    public List<WorkstationDto> find(@RequestParam(required = false) Long roomId) {
        return workstationService.find(roomId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkstationDto create(@Valid @RequestBody WorkstationDto workstation) {
        return workstationService.create(workstation);
    }

    @PutMapping("/{id}")
    public WorkstationDto update(@PathVariable Long id, @Valid @RequestBody WorkstationDto workstation) {
        return workstationService.update(id, workstation);
    }
}
