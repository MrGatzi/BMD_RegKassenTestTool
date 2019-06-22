package sample.Controller;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sample.Util.Configuration;
import sample.Util.PartFilterResult;
import sample.Util.enums.ActionTyp;
import sample.Util.factories.TmpFactory;
import sample.Util.ui.ResultTab;
import sample.Util.factories.AlertFactory;
import sample.Util.errorHandling.BMDExeption;
import sample.Util.enums.Exeptionstyp;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OutputController {


    public TabPane resultTabPane;
    public VBox vbox;
    public AnchorPane anchorpane;
    public HBox hbox;
    public Button filterButton;
    AlertFactory dialogFactory;

    Configuration config;
    TmpFactory tmpFactory;

    public void initialize() {
        dialogFactory = new AlertFactory();
        resultTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            updateNavBar((ResultTab) newTab);
        });
    }

    public ResultTab createNewResultTabPane(String tabName,ActionTyp resultTyp) {
        resultTabPane.prefHeightProperty().bind(vbox.heightProperty());
        ResultTab newTab = new ResultTab(tabName,resultTyp);
        resultTabPane.getTabs().add(newTab);
        resultTabPane.getSelectionModel().select(newTab);
        return newTab;
    }

    public void setConfig(Configuration config) {
        //TODO call this mehtod !
        this.config = config;
        tmpFactory = new TmpFactory(config);
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

    public void onDeletePressed(MouseEvent mouseEvent) throws IOException {
        ((ResultTab) resultTabPane.getSelectionModel().getSelectedItem()).onClose();
        resultTabPane.getTabs().remove(resultTabPane.getSelectionModel().getSelectedItem());
    }

    public void onFilterPressed(MouseEvent mouseEvent) throws IOException {
        //check typ
        if (((ResultTab) resultTabPane.getSelectionModel().getSelectedItem()).getResultTyp() == ActionTyp.RUNDEPTEST) {
            File outputFile = tmpFactory.getNewTmpFile(ActionTyp.FILTERDEPTEST);
            String textToCheck = ((ResultTab) resultTabPane.getSelectionModel().getSelectedItem()).getCurrentlyDisplayedText();
            PartFilterResult PartFilterResult = new PartFilterResult();
            PartFilterResult.addToResultString("Fehler gefunden in Step 1 :\r\n");
            PartFilterResult.add(checkForErrors(textToCheck, "Machine readable code validation #"));
            PartFilterResult.addToResultString("-------------------------------------------------------------------------------\nFehler gefunden in Step 2 :\r\n");
            PartFilterResult.add(checkForErrors(textToCheck, "RKSV-DEP-EXPORT-validation #"));
        }

        //TODO: PrintResult

    }

    public void onShowPressed(MouseEvent mouseEvent) throws IOException {
        Desktop.getDesktop().open(((ResultTab) resultTabPane.getSelectionModel().getSelectedItem()).getFile());
    }

    public void onSharePressed(MouseEvent mouseEvent) throws IOException {
        dialogFactory.createNewDialog(anchorpane, new BMDExeption("This feature is not implemented yet", "Work in progress", Exeptionstyp.INFO)).showAndWait();
    }

    private PartFilterResult checkForErrors(String textToCheck, String checkString) {
        int checkedReceipt = 0;
        int errorsFound = 0;
        int index = textToCheck.indexOf(checkString);
        int index2 = 0;
        int index3 = 0;
        String partToCheck = "";
        StringBuilder errorMessages = new StringBuilder();
        while (index >= 0) {
            checkedReceipt++;
            index2 = textToCheck.indexOf("Machine readable code validation #", index + 1);
            if (index2 != -1) {
                partToCheck = textToCheck.substring(index, index2);
                index3 = partToCheck.indexOf("FAIL");
                if (index3 != -1) {
                    errorsFound++;
                    errorMessages.append(partToCheck);
                    errorMessages.append("\r\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\r\n");
                }
            }
            index = index2;
        }
        PartFilterResult partFilterResult = new PartFilterResult(checkedReceipt, errorsFound, errorMessages.toString());
        return partFilterResult;
    }

    public void updateNavBar(ResultTab newTab) {
        switch (newTab.getResultTyp()) {
            case RUNDEPTEST:
                filterButton.setDisable(false);
                break;
            case SHOWDEPFILE:
                filterButton.setDisable(true);
                break;
            case FILTERDEPTEST:
                filterButton.setDisable(false);
                break;
        }
    }
}
