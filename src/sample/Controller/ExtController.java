package sample.Controller;

import javafx.event.ActionEvent;
import sample.Util.Configuration;
import sample.Util.ui.MenuController;

public class ExtController implements MenuController {
    
    //From FXML


    Configuration config;


    public void initialize() {
        ;
    }

    @Override
    public void setConfig(Configuration config) {
        this.config=config;
    }
}
