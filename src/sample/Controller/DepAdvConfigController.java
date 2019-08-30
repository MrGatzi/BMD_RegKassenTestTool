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
import sample.Util.DepLogic.AdvDepTestLogic;
import sample.Util.DepLogic.Results.AdvResult;
import sample.Util.Enums.ResultTyp;
import sample.Util.Factories.TmpFactory;
import sample.Util.Ui.CostumComboBoxItem;
import sample.Util.Ui.MenuController;
import sample.Util.Ui.ResultTab;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class DepAdvConfigController implements MenuController {

    public JFXTextField folderKeyFile;
    public JFXComboBox<CostumComboBoxItem> nameAdvKeyFile;
    public JFXTextField folderAdvDepFile;
    public JFXComboBox<CostumComboBoxItem> nameAdvDepFile;
    public Button runWithoutTestButton;
    public AnchorPane ParentPane;
    public VBox input;
    public VBox action;
    public Button runWithTestButton;
    public HBox DepShowCheckboxs;
    public HBox inputLine;
    public JFXCheckBox startReceiptBox;
    public JFXCheckBox futureBox;
    public JFXCheckBox splitIntoDepFilesBox;
    public HBox DepShowCheckboxs2;
    public JFXCheckBox detailsBox;

    OutputController outputController;
    Configuration config;
    List<CostumComboBoxItem> advDepFiles;
    List<CostumComboBoxItem> advDepKeyFiles;
    AdvDepTestLogic advDepTestLogic;
    TmpFactory tmpFactory;

    public void initialize() {
        inputLine.prefWidthProperty().bind(ParentPane.widthProperty());
        folderKeyFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        folderAdvDepFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        nameAdvDepFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        nameAdvKeyFile.prefWidthProperty().bind(ParentPane.widthProperty().divide(2));
        DepShowCheckboxs.maxWidthProperty().bind(ParentPane.widthProperty().subtract(10));
        DepShowCheckboxs2.maxWidthProperty().bind(ParentPane.widthProperty().subtract(10));
        setupTextField();

    }

    public void shutdown(){
        //TODO SHUT DOWN METHODS!
        this.config.setAdvDepFiles(convertToStringItems(nameAdvDepFile.getItems()));
        this.config.setAdvDepKeyFiles(convertToStringItems(nameAdvKeyFile.getItems()));
    }


    public void setOutputController(OutputController outputController) {
        this.outputController = outputController;
    }

    public void setConfig(Configuration config) {
        this.config = config;
        advDepFiles = converteToComboBoxItems(config.getAdvDepFiles());
        advDepKeyFiles = converteToComboBoxItems(config.getAdvDepKeyFiles());

        setSavedFileNames(advDepFiles, nameAdvDepFile);
        setSavedFileNames(advDepKeyFiles, nameAdvKeyFile);
        advDepTestLogic = new AdvDepTestLogic(config);
        tmpFactory = new TmpFactory(config);
    }

    public void chooseDepFile(MouseEvent mouseEvent) throws IOException {
        addNewFileToChoose(nameAdvDepFile);
    }

    public void openDepFile(MouseEvent mouseEvent) throws IOException {
        openFile(nameAdvDepFile.getSelectionModel().getSelectedItem().getPath());
    }

    public void chooseKeyFile(MouseEvent mouseEvent) throws IOException {
        addNewFileToChoose(nameAdvKeyFile);
    }

    public void openKeyFile(MouseEvent mouseEvent) throws IOException {
        openFile(nameAdvKeyFile.getSelectionModel().getSelectedItem().getPath());
    }

    private void addNewFileToChoose(ComboBox nameField) {
        FileChooser chooseFile = new FileChooser();
        chooseFile.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json Files", "*.json"));
        setInitialDirectory(chooseFile);
        File selectedFile = chooseFile.showOpenDialog(nameAdvKeyFile.getScene().getWindow());
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
            if(!file.equals("")) {
                items.add(new CostumComboBoxItem(file));
            }
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
        nameAdvKeyFile.valueProperty().addListener(new ChangeListener<CostumComboBoxItem>() {
            @Override
            public void changed(ObservableValue ov, CostumComboBoxItem olditem, CostumComboBoxItem newitem) {
                if(newitem!=null) {
                    folderKeyFile.setText(newitem.getPathTo());
                }
            }
        });

        nameAdvDepFile.valueProperty().addListener(new ChangeListener<CostumComboBoxItem>() {
            @Override
            public void changed(ObservableValue ov, CostumComboBoxItem olditem, CostumComboBoxItem newitem) {
                if(newitem!=null) {
                    folderAdvDepFile.setText(newitem.getPathTo());
                }
            }
        });
    }

    private void setInitialDirectory(FileChooser fileChooser) {
        File file = new File(config.getStartFolder());
        if (file.exists()) {
            fileChooser.setInitialDirectory(file);
        }
    }

    public void withoutDepTest() throws IOException {
        //TODO CHECK for better possibility
        File tmpFile = tmpFactory.getNewTmpFile(ResultTyp.ADVDEPTEST);
        ResultTab resultTab = outputController.createNewResultTabPane(tmpFile.getName(), ResultTyp.SHOWDEPFILE);
        resultTab.showLoading();
        //todo ask if split
        Thread t = new Thread(() -> {
            try {
                AdvResult advResult=advDepTestLogic.runAdvDepTest(
                        nameAdvDepFile.getSelectionModel().getSelectedItem().getPath(),
                        nameAdvKeyFile.getSelectionModel().getSelectedItem().getPath(),
                        startReceiptBox.isSelected(),
                        tmpFile,
                        futureBox.isDisableVisualFocus(),
                        detailsBox.isSelected(),
                        false);
                resultTab.printResult(advResult);
            } catch (IOException  | ParseException e) {
                //TODO ERROR HANDLING !
                e.printStackTrace();
            }

        });
        t.start();
    }


    public void withDepTest(ActionEvent actionEvent) throws IOException {
        //TODO CHECK for better possibility
        File tmpFile = tmpFactory.getNewTmpFile(ResultTyp.ADVDEPTEST);
        ResultTab resultTab = outputController.createNewResultTabPane(tmpFile.getName(), ResultTyp.SHOWDEPFILE);
        resultTab.showLoading();
        //todo ask if split
        Thread t = new Thread(() -> {
            try {
                AdvResult advResult=advDepTestLogic.runAdvDepTest(
                        nameAdvDepFile.getSelectionModel().getSelectedItem().getPath(),
                        nameAdvKeyFile.getSelectionModel().getSelectedItem().getPath(),
                        startReceiptBox.isSelected(),
                        tmpFile,
                        futureBox.isDisableVisualFocus(),
                        detailsBox.isSelected(),
                        true);
                resultTab.printResult(advResult);
            } catch (IOException  | ParseException e) {
                //TODO ERROR HANDLING !
                e.printStackTrace();
            }

        });
        t.start();

    }
}