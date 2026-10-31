package pl.pollub.inwentaryzacja.client.model;

import java.util.List;

public record Inventory(List<Room> rooms, List<Workstation> workstations, List<Equipment> equipment) {
}
