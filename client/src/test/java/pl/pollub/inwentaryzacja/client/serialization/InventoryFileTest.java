package pl.pollub.inwentaryzacja.client.serialization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.pollub.inwentaryzacja.client.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryFileTest {

    private final InventoryFile inventoryFile = new InventoryFile();

    private final Inventory inventory = new Inventory(
            List.of(new Room(1L, "E203", null)),
            List.of(new Workstation(1L, 1L, "1", null)),
            List.of(new Equipment(1L, 1L, EquipmentType.BOARD, "NUCLEO-L476RG", "STM32L476RG", EquipmentStatus.WORKING)));

    @Test
    void givenXmlExtension_whenSaveAndLoad_thenXmlFileWithSameInventory(@TempDir Path folder) throws IOException {
        Path path = folder.resolve("inwentarz.xml");

        inventoryFile.save(path, inventory);

        assertTrue(Files.readString(path).startsWith("<"));
        assertEquals(inventory, inventoryFile.load(path));
    }

    @Test
    void givenJsonExtension_whenSaveAndLoad_thenJsonFileWithSameInventory(@TempDir Path folder) throws IOException {
        Path path = folder.resolve("inwentarz.json");

        inventoryFile.save(path, inventory);

        assertTrue(Files.readString(path).startsWith("{"));
        assertEquals(inventory, inventoryFile.load(path));
    }

    @Test
    void givenMissingFile_whenLoad_thenExceptionWithFileName(@TempDir Path folder) {
        Path path = folder.resolve("brak.json");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryFile.load(path));

        assertEquals("Nie udało się odczytać pliku brak.json", exception.getMessage());
    }

    @Test
    void givenBrokenFile_whenLoad_thenExceptionWithFileName(@TempDir Path folder) throws IOException {
        Path path = folder.resolve("zepsuty.json");
        Files.writeString(path, "{ to nie jest json");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryFile.load(path));

        assertEquals("Nie udało się odczytać pliku zepsuty.json", exception.getMessage());
    }
}
