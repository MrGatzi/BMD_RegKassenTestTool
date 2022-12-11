package com.bmd_regkassentesttool.Util.Factories;
import com.bmd_regkassentesttool.Util.ErrorHandling.BMDExeption;
import javafx.scene.control.Alert;

public class AlertFactory {
    public AlertFactory() {

    }

    public void createNewDialog(BMDExeption exeption) {
        Alert alert = switch (exeption.getTyp()) {
            case ERROR -> new Alert(Alert.AlertType.ERROR);
            case INFO -> new Alert(Alert.AlertType.INFORMATION);
            case WARNING -> new Alert(Alert.AlertType.WARNING);
            default -> new Alert(Alert.AlertType.ERROR);
        };

        alert.setTitle(exeption.getTitle());
        alert.setHeaderText(exeption.getTitle());
        alert.setContentText(exeption.getMessage());
        alert.showAndWait();
    }
}
