package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pollub.inwentaryzacja.client.model.Workstation;

public class WorkstationDialog extends FormDialog<Workstation> {

    public WorkstationDialog(String title, Workstation workstation) {
        super(title);
        TextField numberField = new TextField(workstation.number());
        TextField notesField = new TextField(workstation.notes());

        form.addRow(0, new Label("Numer"), numberField);
        form.addRow(1, new Label("Uwagi"), notesField);
        setResultConverter(button -> button == SAVE
                ? new Workstation(workstation.id(), workstation.roomId(), numberField.getText(), notesField.getText())
                : null);
    }
}
