package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pollub.inwentaryzacja.client.model.Equipment;
import pl.pollub.inwentaryzacja.client.model.EquipmentStatus;

public class EditEquipmentDialog extends FormDialog<Equipment> {

    public EditEquipmentDialog(Equipment equipment) {
        super("Edycja sprzętu");
        TextField nameField = new TextField(equipment.name());
        TextField microcontrollerField = new TextField(equipment.microcontroller());
        ComboBox<EquipmentStatus> statusBox = new ComboBox<>();
        statusBox.getItems().setAll(EquipmentStatus.values());
        statusBox.setValue(equipment.status());

        form.addRow(0, new Label("Nazwa"), nameField);
        form.addRow(1, new Label("Mikrokontroler"), microcontrollerField);
        form.addRow(2, new Label("Stan"), statusBox);
        setResultConverter(button -> button == SAVE
                ? new Equipment(equipment.id(), equipment.workstationId(), equipment.type(), nameField.getText(),
                        microcontrollerField.getText(), statusBox.getValue())
                : null);
    }
}
