package pl.pollub.inwentaryzacja.client.ui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pollub.inwentaryzacja.client.model.Equipment;
import pl.pollub.inwentaryzacja.client.model.EquipmentStatus;
import pl.pollub.inwentaryzacja.client.model.EquipmentType;

public class AddEquipmentDialog extends FormDialog<Equipment> {

    public AddEquipmentDialog(Long workstationId) {
        super("Nowy sprzęt");
        ComboBox<EquipmentType> typeBox = new ComboBox<>();
        typeBox.getItems().setAll(EquipmentType.values());
        typeBox.setValue(EquipmentType.BOARD);
        TextField nameField = new TextField();
        TextField microcontrollerField = new TextField();

        form.addRow(0, new Label("Typ"), typeBox);
        form.addRow(1, new Label("Nazwa"), nameField);
        form.addRow(2, new Label("Mikrokontroler"), microcontrollerField);
        setResultConverter(button -> button == SAVE
                ? new Equipment(null, workstationId, typeBox.getValue(), nameField.getText(),
                        microcontrollerField.getText(), EquipmentStatus.WORKING)
                : null);
    }
}
