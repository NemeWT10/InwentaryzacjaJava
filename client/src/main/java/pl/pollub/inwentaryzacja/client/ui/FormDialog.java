package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

public class FormDialog<T> extends Dialog<T> {

    protected static final ButtonType SAVE = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
    private static final ButtonType CANCEL = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
    private static final int SPACING = 10;

    protected final GridPane form = new GridPane(SPACING, SPACING);

    protected FormDialog(String title) {
        setTitle(title);
        getDialogPane().setContent(form);
        getDialogPane().getButtonTypes().addAll(SAVE, CANCEL);
    }
}
