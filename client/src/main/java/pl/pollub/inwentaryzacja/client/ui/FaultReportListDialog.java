package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import pl.pollub.inwentaryzacja.client.api.ApiClient;
import pl.pollub.inwentaryzacja.client.model.FaultReport;
import pl.pollub.inwentaryzacja.client.model.FaultReportStatus;

import java.util.Map;

public class FaultReportListDialog extends Dialog<Void> {

    private static final int SPACING = 10;
    private static final int LIST_WIDTH = 500;

    private final ApiClient apiClient;
    private final ListView<FaultReport> reportList = new ListView<>();
    private final Label messageLabel = new Label();

    public FaultReportListDialog(ApiClient apiClient) {
        this.apiClient = apiClient;

        Button closeReportButton = new Button("Zamknij zgłoszenie");
        closeReportButton.setOnAction(event -> closeSelectedReport());
        closeReportButton.disableProperty().bind(reportList.getSelectionModel().selectedItemProperty().isNull());
        reportList.setPrefWidth(LIST_WIDTH);

        setTitle("Otwarte zgłoszenia usterek");
        getDialogPane().setContent(new VBox(SPACING, reportList, closeReportButton, messageLabel));
        getDialogPane().getButtonTypes().add(new ButtonType("Wróć", ButtonBar.ButtonData.CANCEL_CLOSE));

        showOpenReports();
    }

    private void showOpenReports() {
        String path = "/api/fault-reports?status=" + FaultReportStatus.OPEN;
        ServerCall.run(() -> apiClient.get(path, FaultReport[].class),
                reports -> reportList.getItems().setAll(reports), messageLabel);
    }

    private void closeSelectedReport() {
        FaultReport report = reportList.getSelectionModel().getSelectedItem();
        Map<String, FaultReportStatus> statusChange = Map.of("status", FaultReportStatus.CLOSED);
        ServerCall.run(() -> apiClient.put("/api/fault-reports/" + report.id(), statusChange),
                this::showOpenReports, messageLabel);
    }
}
