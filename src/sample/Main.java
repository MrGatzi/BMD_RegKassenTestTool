package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.Controller.MainController;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.Security;


public class Main extends Application {

    MainController rootController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.getIcons().add(new Image("sample/resources/Images/BMD_RegKassenTestTool_Logo.png"));
        FXMLLoader rootloader = new FXMLLoader(getClass().getResource("resources/fxml/MainMenu.fxml"));
        System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        Parent root = rootloader.load();
        this.rootController = rootloader.<MainController>getController();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        primaryStage.setTitle("BMD_RegKassenTestTool");
        primaryStage.setScene(new Scene(root, 1400, 900));
        primaryStage.show();

    }
    @Override
    public void stop() {
        try {
            rootController.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
