package pl.pollub.inwentaryzacja.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.inwentaryzacja.server.dto.InventoryDto;
import pl.pollub.inwentaryzacja.server.service.InventoryService;

@Tag(name = "Inwentarz", description = "Import i eksport całego inwentarza (JSON/XML)")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Eksport inwentarza (LECTURER)")
    @GetMapping("/export")
    public InventoryDto export() {
        return inventoryService.export();
    }

    @Operation(summary = "Import inwentarza jako nowych rekordów (LECTURER)")
    @PostMapping("/import")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void importInventory(@Valid @RequestBody InventoryDto inventory) {
        inventoryService.importInventory(inventory);
    }
}
