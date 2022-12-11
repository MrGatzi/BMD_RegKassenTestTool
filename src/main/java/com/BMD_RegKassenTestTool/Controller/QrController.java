package com.bmd_regkassentesttool.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import com.bmd_regkassentesttool.Main;
import com.bmd_regkassentesttool.Util.Configuration;
import com.bmd_regkassentesttool.Util.Ui.MenuController;

import java.io.IOException;

public class QrController implements MenuController {
    public AnchorPane configPane;
    public AnchorPane outputPane;
    OutputController outputController;
    QrConfigController configController;

    Configuration config;

    public void initialize() {
        try {
            FXMLLoader outputLoader= new FXMLLoader(Main.class.getResource("fxml/Ouputscreen.fxml"));
            AnchorPane output= outputLoader.load();
            outputPane.getChildren().add(output);
            outputController = outputLoader.<OutputController>getController();

            FXMLLoader configLoader= new FXMLLoader(Main.class.getResource("fxml/Configs/QrConfig.fxml"));
            AnchorPane config = configLoader.load();
            configPane.getChildren().add(config);
            configController = configLoader.<QrConfigController>getController();

            configController.setOutputController(outputController);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfig( Configuration config){
        this.config=config;
        configController.setConfig(this.config);
        outputController.setConfig(this.config);
    }

    public void shutdown(){
        configController.shutdown();
        outputController.shutdown();
    }

}
