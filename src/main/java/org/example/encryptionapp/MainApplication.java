package org.example.encryptionapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Шифровальщик");
        primaryStage.setScene(new Scene(root, 1080, 610));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("O:\\Projects\\EncryptionApp\\src\\main\\java\\org\\example\\encryptionapp\\Password.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}