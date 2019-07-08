package sample.Controller;

import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import sample.Util.*;
import sample.Util.Enums.Exeptionstyp;
import sample.Util.ErrorHandling.BMDExeption;
import sample.Util.Factories.AlertFactory;
import sample.Util.Ui.MenuItem;
import sample.Util.Ui.SplitPaneItem;

import java.io.IOException;

public class MainController<called> {

    public AnchorPane toolPane;
    public ImageView settingsIcon;
    public BorderPane configPane;

    public ImageView depIcon;
    public ImageView depAdvIcon;
    public ImageView qrIcon;
    public ImageView extIcon;
    public SplitPane grandParent;

    Configuration config;
    AlertFactory dialogFactory;

    SplitPaneItem depMenu;
    SplitPaneItem depAdvMenu;
    SplitPaneItem qrMenu;
    SplitPaneItem extMenu;
    SplitPaneItem settingsMenu;

    MenuItem currentItem;

    public void initialize() throws IOException {
        configPane.setMaxWidth(100);
        configPane.setMinWidth(100);

        dialogFactory = new AlertFactory();

        readConfig();
        setUpLoaderAndPanes();
        setMenuPane(depMenu);
    }

    //TODO: CHECK when called
    public void shutdown() throws IOException {
        depMenu.getController().shutdown();
        qrMenu.getController().shutdown();
        settingsMenu.getController().shutdown();
        depAdvMenu.getController().shutdown();
        extMenu.getController().shutdown();
        safeConfig();
    }

    private void readConfig() throws IOException {
        this.config = Configuration.create();
    }

    private void safeConfig() throws IOException {
        config.safe();
    }

    private void setUpLoaderAndPanes() throws IOException {
       depMenu = new SplitPaneItem("resources/fxml/Menus/DepMenu.fxml",this.config);
       qrMenu = new SplitPaneItem("resources/fxml/Menus/QrMenu.fxml",this.config);
       settingsMenu = new SplitPaneItem("resources/fxml/Menus/SettingsMenu.fxml",this.config);
       depAdvMenu = new SplitPaneItem("resources/fxml/Menus/DepAdvMenu.fxml",this.config);
       extMenu = new SplitPaneItem("resources/fxml/Menus/ExtMenu.fxml",this.config);
    }

    private void setMenuPane(MenuItem menuPane) {
        if (currentItem != null) {
            toolPane.getChildren().remove(currentItem.getPane());
        }
        currentItem = menuPane;
        toolPane.getChildren().add(currentItem.getPane());
    }

    public void showDepMenuPressed() {
        setMenuPane(depMenu);

    }

    public void showQrMenuPressed() {
        setMenuPane(qrMenu);
    }

    public void showSettingsMenuPressed() {
        setMenuPane(settingsMenu);
    }

    public void showAdvMenuPressed() {
        setMenuPane(depAdvMenu);
    }

    public void showExtMenuPressed() throws IOException {
        dialogFactory.createNewDialog(grandParent,new BMDExeption("test msg2","title2", Exeptionstyp.INFO)).showAndWait();
        setMenuPane(extMenu);
    }

}
