package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pl.pollub.inwentaryzacja.client.api.FaultReportRequest;
import pl.pollub.inwentaryzacja.client.model.Workstation;

public class ReportFaultDialog extends FormDialog<FaultReportRequest> {

    private static final int DESCRIPTION_ROWS = 4;

    public ReportFaultDialog(Workstation workstation) {
        super("Zgłoszenie usterki");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(DESCRIPTION_ROWS);
        descriptionArea.setWrapText(true);

        form.addRow(0, new Label("Stanowisko"), new Label(workstation.number()));
        form.addRow(1, new Label("Opis usterki"), descriptionArea);
        setResultConverter(button -> button == SAVE
                ? new FaultReportRequest(workstation.id(), descriptionArea.getText())
                : null);
    }
}
