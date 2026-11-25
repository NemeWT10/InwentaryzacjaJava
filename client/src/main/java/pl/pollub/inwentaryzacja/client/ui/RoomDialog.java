package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pollub.inwentaryzacja.client.model.Room;

public class RoomDialog extends FormDialog<Room> {

    public RoomDialog() {
        super("Nowa sala");
        TextField numberField = new TextField();
        TextField descriptionField = new TextField();

        form.addRow(0, new Label("Numer"), numberField);
        form.addRow(1, new Label("Opis"), descriptionField);
        setResultConverter(button -> button == SAVE
                ? new Room(null, numberField.getText(), descriptionField.getText())
                : null);
    }
}
