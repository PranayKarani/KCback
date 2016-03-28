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

    // TODO status: "1 row" if only 1 row not "1 rows"
    // TODO "Show my time table" for staff
    // TODO fix tabbing in student insert
    // TODO add "processing..." UI while sending notice (or progress update)
    // TODO SQL query buttons in launcher screen
    // TODO check for internet connection before doing anything

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
