package pl.pollub.inwentaryzacja.client.serialization;

import org.junit.jupiter.api.Test;
import pl.pollub.inwentaryzacja.client.model.*;
import tools.jackson.core.JacksonException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventorySerializerTest {

    private final InventorySerializer serializer = new InventorySerializer();

    private final Inventory inventory = new Inventory(
            List.of(new Room(1L, "E203", "Pracownia systemów wbudowanych")),
            List.of(new Workstation(1L, 1L, "1", "Brak zasilacza")),
            List.of(new Equipment(1L, 1L, EquipmentType.BOARD, "NUCLEO-L476RG", "STM32L476RG", EquipmentStatus.WORKING),
                    new Equipment(2L, 1L, EquipmentType.CABLE, "Kabel USB", null, EquipmentStatus.BROKEN)));

    @Test
    void givenInventory_whenToJsonAndFromJson_thenSameInventory() {
        String json = serializer.toJson(inventory);

        assertEquals(inventory, serializer.fromJson(json));
    }

    @Test
    void givenInventory_whenToXmlAndFromXml_thenSameInventory() {
        String xml = serializer.toXml(inventory);

        assertEquals(inventory, serializer.fromXml(xml));
    }

    @Test
    void givenInventory_whenToJson_thenStatusWrittenAsName() {
        String json = serializer.toJson(inventory);

        assertTrue(json.contains("\"status\" : \"WORKING\""));
    }

    @Test
    void givenInvalidJson_whenFromJson_thenThrowException() {
        assertThrows(JacksonException.class, () -> serializer.fromJson("{ to nie jest json"));
    }
}
