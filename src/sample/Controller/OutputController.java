package sample.Controller;

import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sample.Util.uiTools.ResultTab;
import sample.Util.factories.AlertFactory;
import sample.Util.errorHandling.BMDExeption;
import sample.Util.enums.Exeptionstyp;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class OutputController {


    public TabPane resultTabPane;
    public VBox vbox;
    public AnchorPane anchorpane;
    public HBox hbox;
    AlertFactory dialogFactory;

    public void initialize() {
        dialogFactory = new AlertFactory();
    }

    public ResultTab createNewResultTabPane(String tabName) {
        resultTabPane.prefHeightProperty().bind(vbox.heightProperty());
        ResultTab newTab = new ResultTab(tabName);
        resultTabPane.getTabs().add(newTab);
        resultTabPane.getSelectionModel().select(newTab);
        return newTab;
    }

    public void filterResults() {
        //TODO: implement
    }

    public void openResult() {
        //TODO: implement
    }

    public void onSavePressed(MouseEvent mouseEvent) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text", "txt");
        chooser.getExtensionFilters().add(filter);
        File file = chooser.showSaveDialog(resultTabPane.getScene().getWindow());
        if (file != null) {
            File newfile = new File(file + ".txt");
            ResultTab selectedTab = (ResultTab) resultTabPane.getSelectionModel().getSelectedItem();
            selectedTab.getFile().renameTo(newfile);
        }
    }

    public void ondeletePressed(MouseEvent mouseEvent) throws IOException {
        ((ResultTab) resultTabPane.getSelectionModel().getSelectedItem()).onClose();
        resultTabPane.getTabs().remove(resultTabPane.getSelectionModel().getSelectedItem());
    }

    public void onfilterPressed(MouseEvent mouseEvent) throws IOException {
        dialogFactory.createNewDialog(anchorpane, new BMDExeption("This feature is not implemented yet", "Work in progress", Exeptionstyp.INFO)).showAndWait();
    }

    public void onShowPressed(MouseEvent mouseEvent) throws IOException {
        Desktop.getDesktop().open(((ResultTab) resultTabPane.getSelectionModel().getSelectedItem()).getFile());
    }

    public void onSharePressed(MouseEvent mouseEvent) throws IOException {
        dialogFactory.createNewDialog(anchorpane, new BMDExeption("This feature is not implemented yet", "Work in progress", Exeptionstyp.INFO)).showAndWait();
    }
}
