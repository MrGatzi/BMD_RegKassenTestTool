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
import sample.Util.depLogic.DepShowResult;
import sample.Util.depLogic.DepTestLogic;
import sample.Util.depLogic.DepTestResult;
import sample.Util.enums.ResultTyp;
import sample.Util.factories.TmpFactory;
import sample.Util.ui.CostumComboBoxItem;
import sample.Util.ui.MenuController;
import sample.Util.ui.ResultTab;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class QrConfigController implements MenuController {

    public JFXTextField folderKeyFile;
    public JFXComboBox<CostumComboBoxItem> nameKeyFile;
    public JFXTextField folderQrFile;
    public JFXComboBox<CostumComboBoxItem> nameQrFile;
    public Button DepShowButton;
    public AnchorPane ParentPane;
    public VBox input;
    public VBox action;
    public Button DepTestButton;
    public HBox DepShowCheckboxs;
    public HBox DepTesCheckboxs;
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
        folderQrFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        nameQrFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        nameKeyFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        DepShowCheckboxs.maxWidthProperty().bind(ParentPane.widthProperty().subtract(10));
        DepTesCheckboxs.maxWidthProperty().bind(ParentPane.widthProperty().subtract(10));
        setupTextField();

    }

    public void shutdown() {
        /*this.config.setDepFiles(depFiles);
        this.config.setDepKeyFiles(depKeyFiles);*/
    }


    public void setOutputController(OutputController outputController) {
        this.outputController = outputController;
    }

    public void setConfig(Configuration config) {
        this.config = config;
        depFiles = converteToComboBoxItems(config.getQrFiles());
        depKeyFiles = converteToComboBoxItems(config.getQrKeyFiles());

        setSavedFileNames(depFiles, nameQrFile);
        setSavedFileNames(depKeyFiles, nameKeyFile);
        depTestLogic = new DepTestLogic(config);
        tmpFactory = new TmpFactory(config);
    }

    public void chooseDepFile(MouseEvent mouseEvent) throws IOException {
        addNewFileToChoose(nameQrFile);
    }

    public void openDepFile(MouseEvent mouseEvent) throws IOException {
        openFile(nameQrFile.getSelectionModel().getSelectedItem().getPath());
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

        nameQrFile.valueProperty().addListener(new ChangeListener<CostumComboBoxItem>() {
            @Override
            public void changed(ObservableValue ov, CostumComboBoxItem olditem, CostumComboBoxItem newitem) {
                folderQrFile.setText(newitem.getPathTo());
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
                DepShowResult depShowResult = depTestLogic.runDepTest(
                        nameQrFile.getSelectionModel().getSelectedItem().getPath(),
                        nameKeyFile.getSelectionModel().getSelectedItem().getPath(),
                        futureBox.isSelected(),
                        detailsBox.isSelected(),
                        tmpFile);
                resultTab.printResult(depShowResult);
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
                DepTestResult depTestResult = depTestLogic.decryptAndStructureDepFile(
                        nameQrFile.getSelectionModel().getSelectedItem().getPath(),
                        nameKeyFile.getSelectionModel().getSelectedItem().getPath(),
                        startReceiptBox.isSelected(),
                        tmpFile);
                resultTab.printResult(depTestResult);
            } catch (IOException | NoSuchAlgorithmException | ParseException e) {
                //TODO ERROR HANDLING !
                e.printStackTrace();
            }

        });
        t.start();

    }
}