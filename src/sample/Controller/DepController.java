package sample.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import sample.Main;
import sample.Util.Configuration;
import sample.Util.ui.MenuController;

import java.io.IOException;

public class DepController implements MenuController {
    public AnchorPane configPane;
    public AnchorPane outputPane;
    OutputController outputController;
    DepConfigController configController;

    Configuration config;
    public void initialize() {
        try {
            FXMLLoader outputLoader= new FXMLLoader(Main.class.getResource("resources/fxml/Ouputscreen.fxml"));
            AnchorPane output= outputLoader.load();
            outputPane.getChildren().add(output);
            outputController = outputLoader.<OutputController>getController();

            FXMLLoader configLoader= new FXMLLoader(Main.class.getResource("resources/fxml/Configs/DepConfig.fxml"));
            AnchorPane config = configLoader.load();
            configPane.getChildren().add(config);
            configController = configLoader.<DepConfigController>getController();

            configController.setOutputController(outputController);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfig( Configuration config){
        this.config=config;
        configController.setConfig(this.config);
    }

    public void runDepTest() {
    }

}
