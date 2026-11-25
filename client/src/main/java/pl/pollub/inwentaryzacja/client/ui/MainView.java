package pl.pollub.inwentaryzacja.client.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pl.pollub.inwentaryzacja.client.api.ApiClient;
import pl.pollub.inwentaryzacja.client.model.Equipment;
import pl.pollub.inwentaryzacja.client.model.Inventory;
import pl.pollub.inwentaryzacja.client.model.Role;
import pl.pollub.inwentaryzacja.client.model.Room;
import pl.pollub.inwentaryzacja.client.model.Workstation;
import pl.pollub.inwentaryzacja.client.serialization.InventoryFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainView extends BorderPane {

    private static final int SPACING = 10;

    private final ApiClient apiClient;
    private final ListView<Room> roomList = new ListView<>();
    private final ListView<Workstation> workstationList = new ListView<>();
    private final ListView<Equipment> equipmentList = new ListView<>();
    private final TextField searchField = new TextField();
    private final Label messageLabel = new Label();
    private final InventoryFile inventoryFile = new InventoryFile();

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
        VBox roomColumn = column("Sale", roomList);
        VBox workstationColumn = column("Stanowiska", workstationList);
        VBox equipmentColumn = column("Sprzęt", equipmentList);
        if (role == Role.LECTURER) {
            workstationColumn.getChildren().add(new HBox(SPACING,
                    button("Dodaj", roomList, this::addWorkstation),
                    button("Edytuj", workstationList, this::editWorkstation)));
            equipmentColumn.getChildren().add(new HBox(SPACING,
                    button("Dodaj", workstationList, this::addEquipment),
                    button("Edytuj", equipmentList, this::editEquipment),
                    button("Usuń", equipmentList, this::deleteEquipment)));
            Button faultReportsButton = new Button("Zgłoszenia usterek");
            faultReportsButton.setOnAction(event -> new FaultReportListDialog(apiClient).showAndWait());
            Button exportButton = new Button("Eksport");
            exportButton.setOnAction(event -> exportInventory());
            Button importButton = new Button("Import");
            importButton.setOnAction(event -> importInventory());
            topBar.getChildren().addAll(faultReportsButton, exportButton, importButton);
        }
        if (role == Role.STUDENT) {
            workstationColumn.getChildren().add(button("Zgłoś usterkę", workstationList, this::reportFault));
        }
        if (role == Role.ADMIN) {
            Button addRoomButton = new Button("Dodaj salę");
            addRoomButton.setOnAction(event -> addRoom());
            roomColumn.getChildren().add(addRoomButton);
            Button usersButton = new Button("Użytkownicy");
            usersButton.setOnAction(event -> new UserListDialog(apiClient).showAndWait());
            topBar.getChildren().add(usersButton);
        }
        HBox lists = new HBox(SPACING, roomColumn, workstationColumn, equipmentColumn);

        setPadding(new Insets(SPACING));
        setTop(topBar);
        setCenter(lists);
        setBottom(messageLabel);
        BorderPane.setMargin(lists, new Insets(SPACING, 0, SPACING, 0));

        showRooms();
    }

    private void showRooms() {
        ServerCall.run(() -> apiClient.get("/api/rooms", Room[].class),
                rooms -> roomList.getItems().setAll(rooms), messageLabel);
    }

    private void showWorkstations(Room room) {
        equipmentList.getItems().clear();
        if (room == null) {
            return;
        }
        ServerCall.run(() -> apiClient.get("/api/workstations?roomId=" + room.id(), Workstation[].class),
                workstations -> workstationList.getItems().setAll(workstations), messageLabel);
    }

    private void showEquipment(Workstation workstation) {
        if (workstation == null) {
            return;
        }
        ServerCall.run(() -> apiClient.get("/api/equipment?workstationId=" + workstation.id(), Equipment[].class),
                equipment -> equipmentList.getItems().setAll(equipment), messageLabel);
    }

    private void searchEquipment() {
        workstationList.getSelectionModel().clearSelection();
        String microcontroller = URLEncoder.encode(searchField.getText(), StandardCharsets.UTF_8);
        ServerCall.run(() -> apiClient.get("/api/equipment?microcontroller=" + microcontroller, Equipment[].class),
                equipment -> equipmentList.getItems().setAll(equipment), messageLabel);
    }

    private void refreshEquipment() {
        Workstation workstation = workstationList.getSelectionModel().getSelectedItem();
        if (workstation == null) {
            searchEquipment();
        } else {
            showEquipment(workstation);
        }
    }

    private void addRoom() {
        new RoomDialog()
                .showAndWait()
                .ifPresent(room -> ServerCall.run(
                        () -> apiClient.post("/api/rooms", room),
                        this::showRooms, messageLabel));
    }

    private void addWorkstation() {
        Room room = roomList.getSelectionModel().getSelectedItem();
        new WorkstationDialog("Nowe stanowisko", new Workstation(null, room.id(), "", ""))
                .showAndWait()
                .ifPresent(workstation -> ServerCall.run(
                        () -> apiClient.post("/api/workstations", workstation),
                        () -> showWorkstations(room), messageLabel));
    }

    private void editWorkstation() {
        Room room = roomList.getSelectionModel().getSelectedItem();
        new WorkstationDialog("Edycja stanowiska", workstationList.getSelectionModel().getSelectedItem())
                .showAndWait()
                .ifPresent(workstation -> ServerCall.run(
                        () -> apiClient.put("/api/workstations/" + workstation.id(), workstation),
                        () -> showWorkstations(room), messageLabel));
    }

    private void addEquipment() {
        Workstation workstation = workstationList.getSelectionModel().getSelectedItem();
        new AddEquipmentDialog(workstation.id())
                .showAndWait()
                .ifPresent(equipment -> ServerCall.run(
                        () -> apiClient.post("/api/equipment", equipment),
                        () -> showEquipment(workstation), messageLabel));
    }

    private void editEquipment() {
        new EditEquipmentDialog(equipmentList.getSelectionModel().getSelectedItem())
                .showAndWait()
                .ifPresent(equipment -> ServerCall.run(
                        () -> apiClient.put("/api/equipment/" + equipment.id(), equipment),
                        this::refreshEquipment, messageLabel));
    }

    private void deleteEquipment() {
        Equipment equipment = equipmentList.getSelectionModel().getSelectedItem();
        ButtonType deleteButton = new ButtonType("Usuń", ButtonBar.ButtonData.OK_DONE);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Usunąć „" + equipment.name() + "”?",
                deleteButton, new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE));
        confirmation.setTitle("Usuwanie sprzętu");
        confirmation.setHeaderText(null);
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == deleteButton) {
            ServerCall.run(() -> apiClient.delete("/api/equipment/" + equipment.id()),
                    this::refreshEquipment, messageLabel);
        }
    }

    private void reportFault() {
        new ReportFaultDialog(workstationList.getSelectionModel().getSelectedItem())
                .showAndWait()
                .ifPresent(report -> ServerCall.run(
                        () -> apiClient.post("/api/fault-reports", report),
                        () -> messageLabel.setText("Zgłoszenie wysłane"), messageLabel));
    }

    private void exportInventory() {
        File file = fileChooser("Eksport inwentarza").showSaveDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        ServerCall.run(() -> inventoryFile.save(file.toPath(), apiClient.get("/api/inventory/export", Inventory.class)),
                () -> messageLabel.setText("Zapisano plik " + file.getName()), messageLabel);
    }

    private void importInventory() {
        File file = fileChooser("Import inwentarza").showOpenDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        ServerCall.run(() -> apiClient.post("/api/inventory/import", inventoryFile.load(file.toPath())),
                () -> {
                    showRooms();
                    messageLabel.setText("Zaimportowano plik " + file.getName());
                }, messageLabel);
    }

    private static FileChooser fileChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("XML", "*.xml"));
        return chooser;
    }

    private static VBox column(String title, ListView<?> list) {
        return new VBox(SPACING, new Label(title), list);
    }

    private static Button button(String text, ListView<?> requiredSelection, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.disableProperty().bind(requiredSelection.getSelectionModel().selectedItemProperty().isNull());
        return button;
    }
}
