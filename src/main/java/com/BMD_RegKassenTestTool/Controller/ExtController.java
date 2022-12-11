package com.bmd_regkassentesttool.Controller;

import com.bmd_regkassentesttool.Util.Configuration;
import com.bmd_regkassentesttool.Util.Ui.MenuController;

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
