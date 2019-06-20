package sample.Util.ui;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import sample.Util.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ResultTab extends Tab {
    Result result;

    public ResultTab(String filename) {
        super.setText(filename);
        result = null;
        this.setOnClosed(c->{onClose();});
    }

    public void printResult(Result result) throws IOException {
        this.result = result;
        TextArea textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);

        try (BufferedReader br = new BufferedReader(new FileReader(result.getOuputLocation()))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                textArea.appendText(sCurrentLine);
                textArea.appendText("\r\n");
            }
        }

        BorderPane root = new BorderPane();
        root.setCenter(textArea);

        Platform.runLater(()->{this.setContent(root);});
    }

    ;

    public void showLoading() {
        HBox hbox = new HBox();
        JFXSpinner loadingSpinner = new JFXSpinner();
        Label loadingLabel = new Label("Loading ....");
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(loadingSpinner,loadingLabel);
        this.setContent(hbox);
    }

    public Result getResult() {
        return result;
    }

    public File getFile(){
        return result.getOuputLocation();
    }

    public void setFile(File file){
        result.setOuputLocation(file);
    }

    public void onClose(){
        if(result.getOuputLocation().exists()){
            result.getOuputLocation().delete();
        }
    }
}
