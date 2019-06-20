package sample.Util.ui;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

import javafx.scene.layout.AnchorPane;
import sample.Util.Configuration;

public class AnchorPaneItem implements MenuItem {;

    private AnchorPane pane;
    private MenuController controller;
    private FXMLLoader loader;

    public AnchorPaneItem(String xmlFile, Configuration config) throws IOException {
        loader = new FXMLLoader(getClass().getResource(xmlFile));
        pane = loader.load();
        controller= loader.<MenuController>getController();
        controller.setConfig(config);
    }

    public AnchorPane getPane() {
        return pane;
    }

    public void AnchorPane(AnchorPane pane) {
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