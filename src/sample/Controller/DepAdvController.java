package sample.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import sample.Main;
import sample.Util.Configuration;
import sample.Util.Ui.MenuController;

import java.io.IOException;

public class DepAdvController implements MenuController {
    public AnchorPane configPane;
    public AnchorPane outputPane;
    OutputController outputController;
    DepAdvConfigController configController;

    Configuration config;

    public void initialize() {
        try {
            FXMLLoader outputLoader= new FXMLLoader(Main.class.getResource("resources/fxml/Ouputscreen.fxml"));
            AnchorPane output= outputLoader.load();
            outputPane.getChildren().add(output);
            outputController = outputLoader.<OutputController>getController();

            FXMLLoader configLoader= new FXMLLoader(Main.class.getResource("resources/fxml/Configs/DepAdvConfig.fxml"));
            AnchorPane config = configLoader.load();
            configPane.getChildren().add(config);
            configController = configLoader.<DepAdvConfigController>getController();
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
