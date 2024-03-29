package com.bmd_regkassentesttool;

import com.bmd_regkassentesttool.Controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.Security;


public class Main extends Application {

    MainController rootController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //primaryStage.getIcons().add(new Image("../../../resources/com/bmd_regkassentesttool/Images/BMD_RegKassenTestTool_Logo.png"));
        FXMLLoader rootloader = new FXMLLoader(getClass().getResource("fxml/MainMenu.fxml"));
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

        if(args.length==0){
            launch(args);
        }else{
          BatchLogic batchLogic = new BatchLogic();
          batchLogic.start(args);
        }

    }
}
