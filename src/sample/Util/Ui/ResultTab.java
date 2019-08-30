package sample.Util.Ui;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import sample.Util.Result;
import sample.Util.Enums.ResultTabState;
import sample.Util.Enums.ResultTyp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

public class ResultTab extends Tab {
    Result result;
    ResultTyp resultTyp;
    ResultTabState resultTabState;
    CodeArea codeArea;

    public ResultTab(String filename, ResultTyp resultTyp) {

        super.setText(filename.substring(0,filename.indexOf(".")));
        this.result = null;
        this.codeArea = new CodeArea();
        this.resultTyp = resultTyp;

        this.setOnClosed(c -> {
            onClose();
        });
        resultTabState = ResultTabState.CREATED;
    }

    ;

    public ResultTab(String filename, ResultTyp resultTyp, Result result) throws IOException {
        super.setText(filename);
        this.result = null;
        this.codeArea = new CodeArea();
        this.resultTyp = resultTyp;

        this.setOnClosed(c -> {
            onClose();
        });
        printResult(result);
        resultTabState = ResultTabState.CREATED;
    }

    public void printResult(Result result) throws IOException {
        this.result = result;

        StringBuilder textToDisplay = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(result.getOuputLocation()))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                textToDisplay.append(sCurrentLine);
                textToDisplay.append("\r\n");
            }
        }

        resultTabState = ResultTabState.PRINTED;

        codeArea.setWrapText(true);
        codeArea.setEditable(false);
        codeArea.replaceText(0, 0, textToDisplay.toString());
        BorderPane root = new BorderPane();
        codeArea.requestFollowCaret();
        root.setCenter(new VirtualizedScrollPane<>(codeArea));

        Platform.runLater(() -> {
            this.setContent(root);
        });


    }

    public void showLoading() {
        HBox hbox = new HBox();
        JFXSpinner loadingSpinner = new JFXSpinner();
        Label loadingLabel = new Label("Loading ....");
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(loadingSpinner, loadingLabel);
        this.setContent(hbox);
        resultTabState = ResultTabState.LOADING;
    }

    public ResultTabState getResultTabState() {
        return resultTabState;
    }

    public Result getResult() {
        return result;
    }

    public File getFile() {
        return result.getOuputLocation();
    }

    public void setFile(File file) {
        result.setOuputLocation(file);
    }

    public void onClose() {
        if (result.getOuputLocation().exists()) {
            result.getOuputLocation().delete();
        }
    }

    public String getCurrentlyDisplayedText() {
        return this.codeArea.getText();
    }

    public ResultTyp getResultTyp() {
        return this.resultTyp;
    }
}
