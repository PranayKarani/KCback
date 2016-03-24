package com.kc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class KC extends Application {

    public static Stage window;
    public static Class aClass;

    public static void main(String[] args) {
        launch(args);
    }

    // TODO staff login and specific details
    // TODO check for internet connection before doing anything
    // TODO add "processing..." UI while sending notice (or progress update)
    // TODO SQL query buttons in launcher screen

    public static void loadApp() {
        try {
            window.setScene(new Scene(FXMLLoader.load(aClass.getResource("layouts/main.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        aClass = getClass();
        window = primaryStage;
        window.getIcons().add(new Image(getClass().getResourceAsStream("Puzzle.png")));
        window.setResizable(false);
        window.setTitle("KC");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("layouts/login.fxml"))));
        window.show();

    }

}
