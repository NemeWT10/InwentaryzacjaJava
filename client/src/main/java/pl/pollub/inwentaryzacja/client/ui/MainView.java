package pl.pollub.inwentaryzacja.client.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.pollub.inwentaryzacja.client.api.ApiClient;
import pl.pollub.inwentaryzacja.client.model.Equipment;
import pl.pollub.inwentaryzacja.client.model.Role;
import pl.pollub.inwentaryzacja.client.model.Room;
import pl.pollub.inwentaryzacja.client.model.Workstation;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainView extends BorderPane {

    private static final int SPACING = 10;

    private final ApiClient apiClient;
    private final ListView<Room> roomList = new ListView<>();
    private final ListView<Workstation> workstationList = new ListView<>();
    private final ListView<Equipment> equipmentList = new ListView<>();
    private final TextField searchField = new TextField();
    private final Label errorLabel = new Label();

    public MainView(ApiClient apiClient, Role role) {
        this.apiClient = apiClient;

        searchField.setPromptText("Mikrokontroler, np. STM32");
        Button searchButton = new Button("Szukaj sprzętu");
        searchButton.setOnAction(event -> searchEquipment());
        HBox topBar = new HBox(SPACING, new Label("Zalogowano jako: " + role), searchField, searchButton);

        roomList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldRoom, newRoom) -> showWorkstations(newRoom));
        workstationList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldWorkstation, newWorkstation) -> showEquipment(newWorkstation));
        HBox lists = new HBox(SPACING, column("Sale", roomList),
                column("Stanowiska", workstationList), column("Sprzęt", equipmentList));

        setPadding(new Insets(SPACING));
        setTop(topBar);
        setCenter(lists);
        setBottom(errorLabel);
        BorderPane.setMargin(lists, new Insets(SPACING, 0, SPACING, 0));

        showRooms();
    }

    private void showRooms() {
        ServerCall.run(() -> apiClient.get("/api/rooms", Room[].class),
                rooms -> roomList.getItems().setAll(rooms), errorLabel);
    }

    private void showWorkstations(Room room) {
        equipmentList.getItems().clear();
        if (room == null) {
            return;
        }
        ServerCall.run(() -> apiClient.get("/api/workstations?roomId=" + room.id(), Workstation[].class),
                workstations -> workstationList.getItems().setAll(workstations), errorLabel);
    }

    private void showEquipment(Workstation workstation) {
        if (workstation == null) {
            return;
        }
        ServerCall.run(() -> apiClient.get("/api/equipment?workstationId=" + workstation.id(), Equipment[].class),
                equipment -> equipmentList.getItems().setAll(equipment), errorLabel);
    }

    private void searchEquipment() {
        workstationList.getSelectionModel().clearSelection();
        String microcontroller = URLEncoder.encode(searchField.getText(), StandardCharsets.UTF_8);
        ServerCall.run(() -> apiClient.get("/api/equipment?microcontroller=" + microcontroller, Equipment[].class),
                equipment -> equipmentList.getItems().setAll(equipment), errorLabel);
    }

    private static VBox column(String title, ListView<?> list) {
        return new VBox(SPACING, new Label(title), list);
    }
}
