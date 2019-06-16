package sample.Controller;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sample.Util.DepTestResult;
import sample.Util.ResultTab;
import sample.Util.factories.AlertFactory;
import sample.Util.errorHandling.BMDExeption;
import sample.Util.enums.Exeptionstyp;

import java.io.IOException;

public class OutputController {


    public TabPane resultTabPane;
    public VBox vbox;
    public AnchorPane anchorpane;
    public HBox hbox;
    AlertFactory dialogFactory;

    public void initialize() {
        dialogFactory=new AlertFactory();
    }

    public ResultTab createNewResultTabPane(String tabName) {
        resultTabPane.prefHeightProperty().bind(vbox.heightProperty());
        ResultTab newTab=new ResultTab(tabName);
        resultTabPane.getTabs().add(newTab);
        resultTabPane.getSelectionModel().select(newTab);
        return newTab;
    }

    public void safeResult() {
        //TODO: implement
    }

    public void filterResults() {
        //TODO: implement
    }
    public void openResult() {
        //TODO: implement
    }

    public void onSavePressed(MouseEvent mouseEvent) throws IOException {
        dialogFactory.createNewDialog(anchorpane,new BMDExeption("This feature is not implemented yet!","Work in Progress", Exeptionstyp.INFO)).showAndWait();
    }

    public void ondeletePressed(MouseEvent mouseEvent) throws IOException {
        dialogFactory.createNewDialog(anchorpane,new BMDExeption("This feature is not implemented yet!","Work in Progress", Exeptionstyp.INFO)).showAndWait();
    }

    public void onfilterPressed(MouseEvent mouseEvent) throws IOException {
        dialogFactory.createNewDialog(anchorpane,new BMDExeption("This feature is not implemented yet!","Work in Progress", Exeptionstyp.INFO)).showAndWait();
    }

    public void onShowPressed(MouseEvent mouseEvent) throws IOException {
        dialogFactory.createNewDialog(anchorpane,new BMDExeption("This feature is not implemented yet!","Work in Progress", Exeptionstyp.INFO)).showAndWait();
    }

    public void onSharePressed(MouseEvent mouseEvent) throws IOException {
        dialogFactory.createNewDialog(anchorpane,new BMDExeption("This feature is not implemented yet!","Work in Progress", Exeptionstyp.INFO)).showAndWait();
    }
}
