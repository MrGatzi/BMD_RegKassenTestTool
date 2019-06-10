package sample.Util.factories;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Main;
import sample.Util.errorHandling.BMDExeption;

import java.io.IOException;

public class AlertFactory {
    public AlertFactory() {

    }

    public JFXAlert createNewDialog(Node parent, BMDExeption exeption) throws IOException {

        JFXDialogLayout layout = setUpAlertLayout(exeption);

        JFXAlert<Void> alert = new JFXAlert<Void>((Stage) parent.getScene().getWindow());
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.initModality(Modality.NONE);

        JFXButton closeButton = new JFXButton("ACCEPT");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> alert.close());
        layout.setActions(closeButton);

        alert.setContent(layout);
        return alert;
    }

    private JFXDialogLayout setUpAlertLayout(BMDExeption exeption) throws IOException {
        JFXDialogLayout layout = new JFXDialogLayout();
        FXMLLoader layoutLoader;
        AnchorPane layoutPane;
        Label titel;

        switch (exeption.getTyp()) {
            case ERROR:
                layoutLoader = new FXMLLoader(Main.class.getResource("resources/fxml/Dialogs/ErrorHeader.fxml"));
                break;
            case INFO:
                layoutLoader = new FXMLLoader(Main.class.getResource("resources/fxml/Dialogs/InfoHeader.fxml"));
                break;
            case WARNING:
                layoutLoader = new FXMLLoader(Main.class.getResource("resources/fxml/Dialogs/WarningHeader.fxml"));
                break;
            default:
                layoutLoader = new FXMLLoader(Main.class.getResource("resources/fxml/Dialogs/ErrorHeader.fxml"));
        }

        layoutPane = layoutLoader.load();
        titel = (Label) layoutPane.lookup("#ExeptionTitle");
        if (titel != null) titel.setText(exeption.getTitle());

        layout.setHeading(layoutPane);
        layout.setBody(new Label(exeption.getMessage()));

        return layout;
    }
}
