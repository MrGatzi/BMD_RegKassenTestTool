package sample.Controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sample.Util.Configuration;
import sample.Util.DepLogic.Results.TestResult;
import sample.Util.Factories.TmpFactory;
import sample.Util.DepLogic.Results.ShowResult;
import sample.Util.Enums.ResultTyp;
import sample.Util.Ui.ResultTab;
import sample.Util.Ui.CostumComboBoxItem;
import sample.Util.Ui.MenuController;
import sample.Util.DepLogic.DepTestLogic;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class DepConfigController implements MenuController {

    public JFXTextField folderKeyFile;
    public JFXComboBox<CostumComboBoxItem> nameKeyFile;
    public JFXTextField folderDepFile;
    public JFXComboBox<CostumComboBoxItem> nameDepFile;
    public Button depShowButton;
    public AnchorPane ParentPane;
    public VBox input;
    public VBox action;
    public Button DepTestButton;
    public HBox depShowCheckboxs;
    public HBox depTestCheckboxs;
    public HBox inputLine;
    public JFXCheckBox startReceiptBox;
    public JFXCheckBox futureBox;
    public JFXCheckBox detailsBox;

    OutputController outputController;
    Configuration config;
    List<CostumComboBoxItem> depFiles;
    List<CostumComboBoxItem> depKeyFiles;
    DepTestLogic depTestLogic;
    TmpFactory tmpFactory;

    public void initialize() {
        inputLine.prefWidthProperty().bind(ParentPane.widthProperty());
        folderKeyFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        folderDepFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        nameDepFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        nameKeyFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        depShowCheckboxs.maxWidthProperty().bind(ParentPane.widthProperty().subtract(10));
        depTestCheckboxs.maxWidthProperty().bind(ParentPane.widthProperty().subtract(10));
        setupTextField();

    }

    public void shutdown() {
        this.config.setDepFiles(convertToStringItems(nameDepFile.getItems()));
        this.config.setDepKeyFiles(convertToStringItems(nameKeyFile.getItems()));
    }


    public void setOutputController(OutputController outputController) {
        this.outputController = outputController;
    }

    public void setConfig(Configuration config) {
        this.config = config;
        depFiles = converteToComboBoxItems(config.getDepFiles());
        depKeyFiles = converteToComboBoxItems(config.getDepKeyFiles());

        setSavedFileNames(depFiles, nameDepFile);
        setSavedFileNames(depKeyFiles, nameKeyFile);
        depTestLogic = new DepTestLogic(config);
        tmpFactory = new TmpFactory(config);
    }

    public void chooseDepFile(MouseEvent mouseEvent) throws IOException {
        addNewFileToChoose(nameDepFile);
    }

    public void openDepFile(MouseEvent mouseEvent) throws IOException {
        openFile(nameDepFile.getSelectionModel().getSelectedItem().getPath());
    }

    public void chooseKeyFile(MouseEvent mouseEvent) throws IOException {
        addNewFileToChoose(nameKeyFile);
    }

    public void openKeyFile(MouseEvent mouseEvent) throws IOException {
        openFile(nameKeyFile.getSelectionModel().getSelectedItem().getPath());
    }

    private void addNewFileToChoose(ComboBox nameField) {
        FileChooser chooseFile = new FileChooser();
        chooseFile.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json Files", "*.json"));
        setInitialDirectory(chooseFile);
        File selectedFile = chooseFile.showOpenDialog(nameKeyFile.getScene().getWindow());
        if (selectedFile != null) {
            CostumComboBoxItem newItem = new CostumComboBoxItem(selectedFile.getAbsolutePath());
            nameField.getItems().add(newItem);
            if(nameField.getItems().size()>7){
                nameField.getItems().remove(0);
            }
            nameField.getSelectionModel().select(newItem);
        }
    }

    private void setSavedFileNames(List<CostumComboBoxItem> savedDepFileNames, ComboBox<CostumComboBoxItem> field) {
        for (CostumComboBoxItem file : savedDepFileNames) {
            field.getItems().add(file);
        }
        field.getSelectionModel().selectFirst();
    }

    private List<CostumComboBoxItem> converteToComboBoxItems(List<String> savedDepFileNames) {
        List<CostumComboBoxItem> items = new ArrayList<>();

        for (String file : savedDepFileNames) {
            items.add(new CostumComboBoxItem(file));
        }
        return items;
    }

    private List<String> convertToStringItems(List<CostumComboBoxItem> costumComboBox){
        List<String> items = new ArrayList<>();

        for (CostumComboBoxItem item : costumComboBox) {
            items.add(item.getPath());
        }
        return items;
    }

    private void openFile(String fileLocation) throws IOException {
        File myFile = new File(fileLocation);
        Desktop.getDesktop().open(myFile);
    }

    private void setupTextField() {
        nameKeyFile.valueProperty().addListener(new ChangeListener<CostumComboBoxItem>() {
            @Override
            public void changed(ObservableValue ov, CostumComboBoxItem olditem, CostumComboBoxItem newitem) {
                folderKeyFile.setText(newitem.getPathTo());
            }
        });

        nameDepFile.valueProperty().addListener(new ChangeListener<CostumComboBoxItem>() {
            @Override
            public void changed(ObservableValue ov, CostumComboBoxItem olditem, CostumComboBoxItem newitem) {
                folderDepFile.setText(newitem.getPathTo());
            }
        });
    }

    private void setInitialDirectory(FileChooser fileChooser) {
        File file = new File(config.getStartFolder());
        if (file.exists()) {
            fileChooser.setInitialDirectory(file);
        }
    }

    public void runDepTest() throws IOException {
        File tmpFile = tmpFactory.getNewTmpFile(ResultTyp.RUNDEPTEST);
        ResultTab resultTab = outputController.createNewResultTabPane(tmpFile.getName(), ResultTyp.RUNDEPTEST);
        resultTab.showLoading();
        Thread t = new Thread(() -> {
            try {
                ShowResult showResult = depTestLogic.runDepTest(
                        nameDepFile.getSelectionModel().getSelectedItem().getPath(),
                        nameKeyFile.getSelectionModel().getSelectedItem().getPath(),
                        futureBox.isSelected(),
                        detailsBox.isSelected(),
                        tmpFile);
                resultTab.printResult(showResult);
            } catch (IOException e) {
                //TODO HANDLE EXEPTIONS
                e.printStackTrace();
            }

        });
        t.start();
    }


    public void showDepFile(ActionEvent actionEvent) throws IOException {
        //TODO CHECK for better possibility
        File tmpFile = tmpFactory.getNewTmpFile(ResultTyp.SHOWDEPFILE);
        ResultTab resultTab = outputController.createNewResultTabPane(tmpFile.getName(), ResultTyp.SHOWDEPFILE);
        resultTab.showLoading();
        Thread t = new Thread(() -> {
            try {
                TestResult testResult = depTestLogic.decryptAndStructureDepFile(
                        nameDepFile.getSelectionModel().getSelectedItem().getPath(),
                        nameKeyFile.getSelectionModel().getSelectedItem().getPath(),
                        startReceiptBox.isSelected(),
                        tmpFile);
                resultTab.printResult(testResult);
            } catch (IOException | NoSuchAlgorithmException | ParseException e) {
                //TODO ERROR HANDLING !
                e.printStackTrace();
            }

        });
        t.start();

    }
}