package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.pollub.inwentaryzacja.client.api.UserRequest;
import pl.pollub.inwentaryzacja.client.model.Role;
import pl.pollub.inwentaryzacja.client.model.User;

public class UserDialog extends FormDialog<UserRequest> {

    public UserDialog(String title, User user) {
        super(title);
        TextField loginField = new TextField(user.login());
        PasswordField passwordField = new PasswordField();
        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().setAll(Role.values());
        roleBox.setValue(user.role());
        if (user.id() != null) {
            passwordField.setPromptText("puste = bez zmiany");
        }

        form.addRow(0, new Label("Login"), loginField);
        form.addRow(1, new Label("Hasło"), passwordField);
        form.addRow(2, new Label("Rola"), roleBox);
        setResultConverter(button -> button == SAVE
                ? new UserRequest(loginField.getText(), passwordOrNull(passwordField.getText()), roleBox.getValue())
                : null);
    }

    private static String passwordOrNull(String password) {
        return password.isEmpty() ? null : password;
    }
}
