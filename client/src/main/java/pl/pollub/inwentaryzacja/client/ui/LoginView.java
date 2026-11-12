package pl.pollub.inwentaryzacja.client.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.pollub.inwentaryzacja.client.api.ApiClient;
import pl.pollub.inwentaryzacja.client.api.LoginResponse;

import java.util.function.Consumer;

public class LoginView extends VBox {

    private static final int SPACING = 10;
    private static final int FIELD_WIDTH = 250;

    private final ApiClient apiClient;
    private final Consumer<LoginResponse> onLoggedIn;
    private final TextField loginField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label errorLabel = new Label();

    public LoginView(ApiClient apiClient, Consumer<LoginResponse> onLoggedIn) {
        super(SPACING);
        this.apiClient = apiClient;
        this.onLoggedIn = onLoggedIn;

        loginField.setPromptText("Login");
        loginField.setMaxWidth(FIELD_WIDTH);
        passwordField.setPromptText("Hasło");
        passwordField.setMaxWidth(FIELD_WIDTH);
        Button loginButton = new Button("Zaloguj");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(event -> logIn());

        setPadding(new Insets(SPACING));
        setAlignment(Pos.CENTER);
        getChildren().addAll(new Label("Logowanie"), loginField, passwordField, loginButton, errorLabel);
    }

    private void logIn() {
        String login = loginField.getText();
        String password = passwordField.getText();
        ServerCall.run(() -> apiClient.login(login, password), onLoggedIn, errorLabel);
    }
}
