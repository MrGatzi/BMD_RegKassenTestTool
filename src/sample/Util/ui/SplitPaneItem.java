package sample.Util.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import sample.Main;
import sample.Util.Configuration;

import java.io.IOException;

public class SplitPaneItem implements MenuItem {

    private SplitPane pane;
    private MenuController controller;
    private FXMLLoader loader;

    public SplitPaneItem(String fxmlFile, Configuration config) throws IOException {
        loader = new FXMLLoader(Main.class.getResource(fxmlFile));
        pane = loader.load();
        controller= loader.<MenuController>getController();
        controller.setConfig(config);
    }

    public SplitPane getPane() {
        return pane;
    }

    public void setPane(SplitPane pane) {
        this.pane = pane;
    }

    public MenuController getController() {
        return controller;
    }

    public void setController(MenuController controller) {
        this.controller = controller;
    }

    public FXMLLoader getLoader() {
        return loader;
    }

    public void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }
}
