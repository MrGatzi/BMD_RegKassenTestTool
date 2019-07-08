package sample.Controller;

import sample.Util.Configuration;
import sample.Util.Ui.MenuController;

public class ExtController implements MenuController {
    
    //From FXML


    Configuration config;


    public void initialize() {
    }

    @Override
    public void setConfig(Configuration config) {
        this.config=config;
    }

    @Override
    public void shutdown() {

    }
}
