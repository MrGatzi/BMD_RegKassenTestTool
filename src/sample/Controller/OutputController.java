package sample.Controller;

import com.google.common.io.Files;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import sample.Util.Configuration;
import sample.Util.depLogic.FilterResult;
import sample.Util.enums.ResultTabState;
import sample.Util.enums.ResultTyp;
import sample.Util.factories.TmpFactory;
import sample.Util.ui.ResultTab;
import sample.Util.Result;
import sample.Util.factories.AlertFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class OutputController {


    public TabPane resultTabPane;
    public VBox vbox;
    public AnchorPane anchorpane;
    public HBox hbox;
    public Button filterButton;
    public Text checkedTextField;
    AlertFactory dialogFactory;

    Configuration config;
    TmpFactory tmpFactory;

    public void initialize() {
        dialogFactory = new AlertFactory();
        resultTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            updateNavBar((ResultTab) newTab);
        });
    }

    public ResultTab createNewResultTabPane(String tabName, ResultTyp resultTyp) {
        resultTabPane.prefHeightProperty().bind(vbox.heightProperty());
        ResultTab newTab = new ResultTab(tabName, resultTyp);
        resultTabPane.getTabs().add(newTab);
        resultTabPane.getSelectionModel().select(newTab);
        return newTab;
    }
    public ResultTab createNewResultTabPane(String tabName, ResultTyp resultTyp, Result result) throws IOException {
        resultTabPane.prefHeightProperty().bind(vbox.heightProperty());
        ResultTab newTab = new ResultTab(tabName, resultTyp,result);
        resultTabPane.getTabs().add(newTab);
        resultTabPane.getSelectionModel().select(newTab);
        return newTab;
    }

    public void setConfig(Configuration config) {
        this.config = config;
        tmpFactory = new TmpFactory(config);
    }

    public void onSavePressed(MouseEvent mouseEvent) throws IOException {
        ResultTab currentTab=((ResultTab) resultTabPane.getSelectionModel().getSelectedItem());
        if (currentTab.getResultTabState()== ResultTabState.PRINTED) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export File");
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text", "txt");
            chooser.getExtensionFilters().add(filter);
            File file = chooser.showSaveDialog(resultTabPane.getScene().getWindow());
            if (file != null) {
                File newfile = new File(file + ".txt");
                currentTab.getFile().renameTo(newfile);
            }
        }
    }

    public void onDeletePressed(MouseEvent mouseEvent) throws IOException {
        ResultTab currentTab=((ResultTab) resultTabPane.getSelectionModel().getSelectedItem());
        if (currentTab.getResultTabState()== ResultTabState.PRINTED) {
            currentTab.onClose();
            resultTabPane.getTabs().remove(currentTab);
        }
    }

    public void onFilterPressed(MouseEvent mouseEvent) throws IOException {
        ResultTab currentTab=((ResultTab) resultTabPane.getSelectionModel().getSelectedItem());
        if (currentTab.getResultTyp() == ResultTyp.RUNDEPTEST && currentTab.getResultTabState()== ResultTabState.PRINTED) {
            String textToCheck = ((ResultTab) resultTabPane.getSelectionModel().getSelectedItem()).getCurrentlyDisplayedText();
            File outputFile = tmpFactory.getNewTmpFile(ResultTyp.FILTERDEPTEST);
            FilterResult filterResult = new FilterResult();
            filterResult.addToResultString("Fehler gefunden in Step 1 :\r\n");
            filterResult.add(checkForErrors(textToCheck, "Machine readable code validation #"));
            filterResult.addToResultString("-------------------------------------------------------------------------------\nFehler gefunden in Step 2 :\r\n");
            filterResult.add(checkForErrors(textToCheck, "RKSV-DEP-EXPORT-validation #"));
            Files.write(filterResult.getResultString(), outputFile, Charset.forName("UTF-8"));
            filterResult.setOuputLocation(outputFile);
            ResultTab resultTab = createNewResultTabPane(outputFile.getName(), ResultTyp.FILTERDEPTEST, filterResult);
        }

    }

    public void onShowPressed(MouseEvent mouseEvent) throws IOException {
        ResultTab currentTab=((ResultTab) resultTabPane.getSelectionModel().getSelectedItem());
        if (currentTab.getResultTyp() == ResultTyp.RUNDEPTEST && currentTab.getResultTabState()== ResultTabState.PRINTED) {
            Desktop.getDesktop().open(currentTab.getFile());
        }
    }

    private FilterResult checkForErrors(String textToCheck, String checkString) {
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
        FilterResult partFilterResult = new FilterResult(checkedReceipt, errorsFound, errorMessages.toString());
        return partFilterResult;
    }

    public void setCheckedTextField(FilterResult result) {
        checkedTextField.setText("Checked: " + result.getCheckedReceipt() + " Errors: " + result.getErrorsFound());
        hideCheckedTextField(false);
    }

    public void hideCheckedTextField(boolean hide) {
        if (hide) {
            checkedTextField.setText("Checked: XX Errors:XX");
            checkedTextField.setVisible(false);
        } else {
            checkedTextField.setVisible(true);
        }
    }

    public void updateNavBar(ResultTab newTab) {
        switch (newTab.getResultTyp()) {
            case RUNDEPTEST:
                filterButton.setDisable(false);
                hideCheckedTextField(true);
                break;
            case SHOWDEPFILE:
                filterButton.setDisable(true);
                hideCheckedTextField(true);
                break;
            case FILTERDEPTEST:
                filterButton.setDisable(true);
                setCheckedTextField((FilterResult) newTab.getResult());
                break;
        }
    }

}
