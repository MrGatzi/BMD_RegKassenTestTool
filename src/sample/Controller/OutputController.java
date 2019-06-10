package sample.Controller;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sample.TestResult;
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

    public void showResult(TestResult test) {
        resultTabPane.prefHeightProperty().bind(vbox.heightProperty());
        Tab newTab=tabfactory(test.getOutputString());
        resultTabPane.getTabs().add(newTab);
        resultTabPane.getSelectionModel().select(newTab);
        System.out.println(anchorpane.getHeight()+" "+vbox.getHeight()+" "+hbox.getHeight()+ " " +resultTabPane.getHeight());
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

    private Tab tabfactory(String textContent){
        int numTabs = resultTabPane.getTabs().size();
        Tab tab = new Tab("Tab " + (numTabs + 1));
        TextArea textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);
        textArea.setText(textContent);
        BorderPane root = new BorderPane();
        root.setCenter(textArea);

        tab.setContent(root);

        return tab;
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
