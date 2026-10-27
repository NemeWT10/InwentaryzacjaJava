package pl.pollub.inwentaryzacja.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    private static final String WINDOW_TITLE = "Inwentaryzacja płytek";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        Label welcomeLabel = new Label("System inwentaryzacji płytek ewaluacyjnych");
        stage.setScene(new Scene(new StackPane(welcomeLabel), WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.setTitle(WINDOW_TITLE);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
