package pl.pollub.inwentaryzacja.client.serialization;

import pl.pollub.inwentaryzacja.client.model.Inventory;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Format pliku wynika z rozszerzenia: .xml albo JSON. */
public class InventoryFile {

    private final InventorySerializer serializer = new InventorySerializer();

    public void save(Path path, Inventory inventory) {
        String text = isXml(path) ? serializer.toXml(inventory) : serializer.toJson(inventory);
        try {
            Files.writeString(path, text);
        } catch (IOException e) {
            throw new UncheckedIOException("Nie udało się zapisać pliku " + path.getFileName(), e);
        }
    }

    public Inventory load(Path path) {
        try {
            String text = Files.readString(path);
            return isXml(path) ? serializer.fromXml(text) : serializer.fromJson(text);
        } catch (IOException | JacksonException e) {
            throw new IllegalArgumentException("Nie udało się odczytać pliku " + path.getFileName(), e);
        }
    }

    private static boolean isXml(Path path) {
        return path.getFileName().toString().toLowerCase().endsWith(".xml");
    }
}
