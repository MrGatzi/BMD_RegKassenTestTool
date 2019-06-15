package sample.Controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sample.TestResult;
import sample.Util.Configuration;
import sample.Util.uiTools.CostumComboBoxItem;
import sample.Util.uiTools.MenuController;
import sample.logic.__ShowDepFileInConsole;
import sample.logic.depLogic;

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
    public Button DepShowButton;
    public AnchorPane ParentPane;
    public VBox input;
    public VBox action;
    public Button DepTestButton;
    public HBox DepShowCheckboxs;
    public HBox DepTesCheckboxs;
    public HBox inputLine;

    OutputController outputController;
    Configuration config;
    List<CostumComboBoxItem> depFiles;
    List<CostumComboBoxItem> depKeyFiles;

    public void initialize() {
        inputLine.prefWidthProperty().bind(ParentPane.widthProperty());
        folderKeyFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        folderDepFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        nameDepFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        nameKeyFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        DepShowCheckboxs.maxWidthProperty().bind(ParentPane.widthProperty().subtract(10));
        DepTesCheckboxs.maxWidthProperty().bind(ParentPane.widthProperty().subtract(10));
        setupTextField();
    }

    public void shutdown() {
        /*this.config.setDepFiles(depFiles);
        this.config.setDepKeyFiles(depKeyFiles);*/
    }

    public void runDepTest() throws ParseException, IOException, NoSuchAlgorithmException {
        //__ShowDepFileInConsole a= new __ShowDepFileInConsole();
        //a.show(nameDepFile.getSelectionModel().getSelectedItem().getPath(),nameKeyFile.getSelectionModel().getSelectedItem().getPath(),true);
        depLogic a = new depLogic();
        a.decryptAndStructureDepFile(nameDepFile.getSelectionModel().getSelectedItem().getPath(),nameKeyFile.getSelectionModel().getSelectedItem().getPath(),true);
        /*TestResult test = new TestResult();
        test.setOutputString("oiahfoiabfafawpi");
        outputController.showResult(test);*/
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
    private void setInitialDirectory(FileChooser fileChooser){
        File file = new File(config.getStartFolder());
        if (file.exists()){
            fileChooser.setInitialDirectory(file);
        }
    }
}