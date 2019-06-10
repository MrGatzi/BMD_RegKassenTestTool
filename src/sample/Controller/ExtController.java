package sample.Controller;

import javafx.event.ActionEvent;
import sample.Util.Configuration;
import sample.Util.uiTools.MenuController;

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
