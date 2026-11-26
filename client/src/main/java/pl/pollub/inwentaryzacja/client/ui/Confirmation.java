package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

public final class Confirmation {

    private Confirmation() {
    }

    public static boolean ask(String title, String question, String action) {
        ButtonType actionButton = new ButtonType(action, ButtonBar.ButtonData.OK_DONE);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, question,
                actionButton, new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE));
        confirmation.setTitle(title);
        confirmation.setHeaderText(null);
        confirmation.getDialogPane().getStylesheets().add(Theme.STYLESHEET);
        confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return confirmation.showAndWait().orElse(ButtonType.CANCEL) == actionButton;
    }
}
