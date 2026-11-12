package pl.pollub.inwentaryzacja.client.ui;

import javafx.concurrent.Task;
import javafx.scene.control.Label;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ServerCall {

    private ServerCall() {
    }

    public static <T> void run(Supplier<T> request, Consumer<T> onResult, Label errorLabel) {
        errorLabel.setText("");
        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                return request.get();
            }
        };
        task.setOnSucceeded(event -> onResult.accept(task.getValue()));
        task.setOnFailed(event -> errorLabel.setText(task.getException().getMessage()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void run(Runnable request, Runnable onDone, Label errorLabel) {
        run(() -> {
            request.run();
            return null;
        }, result -> onDone.run(), errorLabel);
    }
}
