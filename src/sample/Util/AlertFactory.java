package sample.Util;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class AlertFactory {
    public AlertFactory(){

    }

    public JFXAlert createNewDialog(SplitPane parent, Exeption exeption) throws IOException {

       /* JFXDialogLayout content= new JFXDialogLayout();
        content.setHeading(new Text("Error, No selection"));
        content.setBody(new Text("No student selected"));
        StackPane stackpane = new StackPane();
        JFXDialog dialog =new JFXDialog(stackpane, content, JFXDialog.DialogTransition.CENTER);
        dialog.se
        JFXButton button=new JFXButton("Okay");
        button.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                dialog.close();
            }
        });
        content.setActions(button);
        exeption.getMessage();
        parent.getParent()*/
        FXMLLoader outputLoader= new FXMLLoader(Main.class.getResource("resources/fxml/Dialogs/ErrorHeader.fxml"));
        AnchorPane output= outputLoader.load();
       System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        JFXDialogLayout layout = new JFXDialogLayout();
        File file = new File("src/sample/resources/Images/error.png");
        layout.setHeading(output);
        layout.setBody(new Label("Lorem standard dummyt has survived not ly with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."));
        JFXAlert<Void> alert = new JFXAlert<Void>((Stage) parent.getScene().getWindow());
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);

        alert.initModality(Modality.NONE);
        JFXButton closeButton = new JFXButton("ACCEPT");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> alert.hideWithAnimation());
        layout.setActions(closeButton);
        ///TODO: check if it is warning or Error.

        alert.setContent(layout);
        return alert;
    }
}
