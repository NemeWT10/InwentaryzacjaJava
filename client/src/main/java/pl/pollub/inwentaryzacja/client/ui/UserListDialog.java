package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.pollub.inwentaryzacja.client.api.ApiClient;
import pl.pollub.inwentaryzacja.client.model.Role;
import pl.pollub.inwentaryzacja.client.model.User;

public class UserListDialog extends Dialog<Void> {

    private static final int SPACING = 10;

    private final ApiClient apiClient;
    private final ListView<User> userList = new ListView<>();
    private final Label messageLabel = new Label();

    public UserListDialog(ApiClient apiClient) {
        this.apiClient = apiClient;

        Button addButton = new Button("Dodaj");
        addButton.setOnAction(event -> addUser());
        Button editButton = new Button("Edytuj");
        editButton.setOnAction(event -> editUser());
        editButton.disableProperty().bind(userList.getSelectionModel().selectedItemProperty().isNull());

        setTitle("Użytkownicy");
        getDialogPane().setContent(new VBox(SPACING, userList, new HBox(SPACING, addButton, editButton), messageLabel));
        getDialogPane().getButtonTypes().add(new ButtonType("Wróć", ButtonBar.ButtonData.CANCEL_CLOSE));

        showUsers();
    }

    private void showUsers() {
        ServerCall.run(() -> apiClient.get("/api/users", User[].class),
                users -> userList.getItems().setAll(users), messageLabel);
    }

    private void addUser() {
        new UserDialog("Nowe konto", new User(null, "", Role.STUDENT))
                .showAndWait()
                .ifPresent(user -> ServerCall.run(
                        () -> apiClient.post("/api/users", user),
                        this::showUsers, messageLabel));
    }

    private void editUser() {
        User user = userList.getSelectionModel().getSelectedItem();
        new UserDialog("Edycja konta", user)
                .showAndWait()
                .ifPresent(changes -> ServerCall.run(
                        () -> apiClient.put("/api/users/" + user.id(), changes),
                        this::showUsers, messageLabel));
    }
}
