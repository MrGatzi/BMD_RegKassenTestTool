package com.bmd_regkassentesttool.Util.Ui;

import com.bmd_regkassentesttool.Util.Configuration;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

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