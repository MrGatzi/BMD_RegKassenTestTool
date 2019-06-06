package sample.Controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sample.Util.Configuration;
import sample.Util.MenuController;

public class ExtController implements MenuController {
    
    //From FXML


    Configuration config;


    public void initialize() {
        ;
    }

    @Override
    public void setConfig(Configuration config) {
        this.config=config;
        setSettings();
    }

    public void setSettings(){
    }

    public void setNewStartFolder(ActionEvent actionEvent) {
    }

    public void setNewJunkFolder(ActionEvent actionEvent) {
    }

    public void clearStorage(ActionEvent actionEvent) {
    }

    public void updateProgramm(ActionEvent actionEvent) {
    }
}
