package com.kc.windows; // 22 Mar, 10:42 AM

import com.kc.entity.Notice;
import com.kc.Utilities.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NoticeViewer {

    // UI stuff
    BorderPane mainLayout;
    VBox       box;

    ScrollPane scrollPane;
    Label headerLabel, fromLabel, toLabel, dateTimeLabel, noticeLabel;
    Line line1, line2;


    FlowPane statusBar;
    Label    status;

    public NoticeViewer(Notice notice) {

        int id = notice.getId();

        String query = "SELECT * FROM notice WHERE id = " + id;

        ResultSet resultSet = DatabaseHelper.launchQuery(query);
        //
        headerLabel = new Label("");
        headerLabel.setWrapText(true);
        headerLabel.setStyle(headerStyle());
        headerLabel.setPadding(new Insets(0,0,5,0));
        //
        fromLabel = new Label("From: ");
        toLabel = new Label("To: ");
        //
        dateTimeLabel = new Label("");
        dateTimeLabel.setStyle(datetimeStyle());
        //
        noticeLabel = new Label("");
        noticeLabel.setWrapText(true);
        noticeLabel.setStyle(noticeStyle());
        noticeLabel.setPadding(new Insets(10,0,0,0));

        line1 = new Line(0, 0, 50, 0);
        line2 = new Line(0, 0, 50, 0);

        try {
            if (resultSet.first()) {
                headerLabel.setText(resultSet.getString("header"));
                fromLabel.setText("From: " + resultSet.getString("sender"));
                String to = resultSet.getString("receiver");
                if (to != null) {
                    if (to.equals("0")) {
                        toLabel.setText("To: all students");
                    } else {
                        toLabel.setText("To: sem " + to + " students");
                    }
                }
                String dt = resultSet.getString("time") +
                        ", " +
                        resultSet.getString("date");
                dateTimeLabel.setText(dt);
                noticeLabel.setText(resultSet.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Stage window = new Stage();
        window.setTitle("Notice History");
        window.initModality(Modality.APPLICATION_MODAL);
        box = new VBox();
        box.setPadding(new Insets(10, 10, 10, 10));
        box.setSpacing(2);
        box.getChildren().addAll(headerLabel, fromLabel, toLabel, dateTimeLabel, line1, noticeLabel, line2);
        box.setPrefWidth(600);
        box.setPrefHeight(600);
        scrollPane = new ScrollPane(box);
        mainLayout = new BorderPane(scrollPane, null, null, statusBar, null);
        Scene scene = new Scene(mainLayout);
        window.setResizable(true);
        window.getIcons().add(new Image(getClass().getResourceAsStream("Puzzle.png")));
//        window.setWidth(600);
//        window.setHeight(600);
        window.setScene(scene);
        window.sizeToScene();
        window.show();

    }

    private String headerStyle() {

        return "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: Calibri;" +
                "-fx-text-fill: #333333;";

    }

    private String datetimeStyle() {

        return "-fx-font-size: 12px;" +
                "-fx-font-weight: lighter;" +
                "-fx-font-style: italic;" +
                "-fx-font-family: Calibri;" +
                "-fx-text-fill: #333333;";

    }

    private String noticeStyle() {

        return "-fx-font-size: 18px;" +
                "-fx-font-weight: normal;" +
                "-fx-font-family: Calibri;" +
                "-fx-text-fill: #333333;";

    }

}
