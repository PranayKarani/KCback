package com.kc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class KC extends Application {

    Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    // TODO check for internet connection before doing anything

    @Override
    public void start(Stage primaryStage) throws IOException {
        window = primaryStage;
        window.getIcons().add(new Image(getClass().getResourceAsStream("Puzzle.png")));
        window.setResizable(false);
        window.setTitle("KC");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("layouts/main.fxml"))));
        window.show();

    }

}
