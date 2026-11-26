package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class FormDialog<T> extends Dialog<T> {

    protected static final ButtonType SAVE = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
    private static final ButtonType CANCEL = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
    private static final int SPACING = 10;

    protected final GridPane form = new GridPane(SPACING, SPACING);

    protected FormDialog(String title) {
        setTitle(title);
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(Region.USE_PREF_SIZE);
        form.getColumnConstraints().add(labelColumn);
        getDialogPane().setContent(form);
        getDialogPane().getStylesheets().add(Theme.STYLESHEET);
        getDialogPane().getButtonTypes().addAll(SAVE, CANCEL);
    }
}
