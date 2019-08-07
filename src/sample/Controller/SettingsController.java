package sample.Controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import sample.Util.Configuration;
import sample.Util.Ui.MenuController;
import sample.Util.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SettingsController implements MenuController {

    //From FXML
    public AnchorPane configPane;
    public AnchorPane infoPane;
    public ImageView startFolderButton;
    public ImageView junkFolderButton;
    public JFXTextField startFolderTextField;
    public JFXTextField junkFolderTextField;
    public JFXComboBox depTestRamComboBox;
    public JFXToggleButton saveQuestionEnabledButton;
    public JFXToggleButton popUpEnabledButton;
    public Label versionLable;
    public HBox junkFolderInput;
    public HBox startFolderInput;
    public HBox allInput;
    public SplitPane settingPane;

    Configuration config;
    Version version;

    public void initialize() throws IOException {
        //TODO: FONTSIZE && FONT
        //TODO: ExternalDepToolLocation
        version = new Version();
    }

    @Override
    public void setConfig(Configuration config) {
        this.config = config;
        configPane.maxWidthProperty().bind(settingPane.widthProperty().multiply(0.6));
        configPane.minWidthProperty().bind(settingPane.widthProperty().multiply(0.6));
        infoPane.maxWidthProperty().bind(settingPane.widthProperty().multiply(0.4));
        startFolderInput.prefWidthProperty().bind(configPane.widthProperty());
        setSettings();
    }

    @Override
    public void shutdown() {

    }

    public void setSettings() {
        startFolderTextField.setText(config.getStartFolder());
        junkFolderTextField.setText(config.getJunkFolder());
        saveQuestionEnabledButton.setSelected(config.isAskQuestion());
        popUpEnabledButton.setSelected(config.isPopUp());
        setRamInput(config.getRamInput(),config.getSelectedRamInput(),depTestRamComboBox);
        versionLable.setText(version.getVersionNumber());
    }

    public void setNewStartFolder(ActionEvent actionEvent) {
        String newfile = chooseAndSetNewFolder(startFolderTextField);
        if (newfile != null) {
            config.setStartFolder(newfile);
        }

    }

    public void setNewJunkFolder(ActionEvent actionEvent) {
        String newfile = chooseAndSetNewFolder(junkFolderTextField);
        if (newfile != null) {
            config.setJunkFolder(newfile);
        }
    }

    public void clearStorage(ActionEvent actionEvent) throws IOException {
        FileUtils.cleanDirectory(new File(config.getJunkFolder()));
    }

    public void updateProgramm(ActionEvent actionEvent) {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec("BMD_RegKassenTestToolUpdater.exe");
            System.exit(0);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private String chooseAndSetNewFolder(JFXTextField nameField) {
        DirectoryChooser chooseDirectory = new DirectoryChooser();
        setInitialDirectory(chooseDirectory);
        File selectedDirectory = chooseDirectory.showDialog(configPane.getScene().getWindow());
        if (selectedDirectory != null) {
            nameField.setText(selectedDirectory.getAbsolutePath());
            return selectedDirectory.getAbsolutePath();
        }
        return null;
    }

    public void setPopUp(ActionEvent actionEvent) {
        config.setPopUp(popUpEnabledButton.selectedProperty().get());
    }

    public void setupAskQuestions(ActionEvent actionEvent) {
        config.setAskQuestion(saveQuestionEnabledButton.selectedProperty().get());

    }

    private void setInitialDirectory(DirectoryChooser chooseDirectory) {
        File file = new File(config.getStartFolder());
        if (file.exists()) {
            chooseDirectory.setInitialDirectory(file);
        }
    }

    private void setRamInput(List<String> ramInput,String selectedRamInput, JFXComboBox<String> field) {
        for (String ramNumber : ramInput) {
            field.getItems().add(ramNumber);
        }
        field.getSelectionModel().select(selectedRamInput);
        field.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String olditem, String newitem) {
                config.setSelectedRamInput(newitem);
            }
        });
    }
}
