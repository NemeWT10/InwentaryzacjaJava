package pl.pollub.inwentaryzacja.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.pollub.inwentaryzacja.client.api.ApiClient;
import pl.pollub.inwentaryzacja.client.api.LoginResponse;
import pl.pollub.inwentaryzacja.client.ui.LoginView;
import pl.pollub.inwentaryzacja.client.ui.MainView;

public class ClientApplication extends Application {

    private static final String WINDOW_TITLE = "Inwentaryzacja płytek";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String SERVER_URL = "http://localhost:8080";

    private final ApiClient apiClient = new ApiClient(SERVER_URL);
    private Scene scene;

    @Override
    public void start(Stage stage) {
        scene = new Scene(new LoginView(apiClient, this::showMainView), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle(WINDOW_TITLE);
        stage.show();
    }

    private void showMainView(LoginResponse loginResponse) {
        scene.setRoot(new MainView(apiClient, loginResponse.role()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
