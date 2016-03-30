package com.kc.windows; // 19 Mar, 01:21 AM

import com.kc.Utilities.DatabaseHelper;
import com.kc.entity.Notice;
import com.kc.entity.Staff;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class NoticeHistory {

    // UI stuff
    BorderPane mainLayout;
    VBox       box;

    TableView<Notice>      table;
    ObservableList<Notice> rows;

    FlowPane               statusBar;
    Label                  status;

    boolean all = false;

    public NoticeHistory(boolean all) throws IOException {

        this.all = all;

        Stage window = new Stage();
        window.setTitle("Notice History");
        window.initModality(Modality.APPLICATION_MODAL);

        // Setting up status bar
        status = new Label("Status bar");
        statusBar = new FlowPane(Orientation.HORIZONTAL, status);
        statusBar.setStyle("-fx-background-color: #e8e8e8");
        statusBar.setPadding(new Insets(2, 2, 2, 2));

        // setting up Table
        table = new TableView<>();
        table.setPrefSize(800,600);
        rows = FXCollections.observableArrayList();
        table.setItems(rows);
        table.setEditable(false);
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            new NoticeViewer(newValue);

        });

        // setting up table columns
        TableColumn<Notice, String> header = new TableColumn<>("Header");
        header.setCellValueFactory(new PropertyValueFactory("header"));
        header.setPrefWidth(400);
        header.setResizable(false);
        // from column
        TableColumn<Notice, String> from = new TableColumn<>("From");
        from.setCellValueFactory(new PropertyValueFactory("from"));
        from.setPrefWidth(200);
        from.setResizable(false);
        // Tp column
        TableColumn<Notice, String> to = new TableColumn<>("To");
        to.setCellValueFactory(new PropertyValueFactory("to"));
        to.setPrefWidth(50);
        to.setResizable(false);
        // date column
        TableColumn<Notice, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(new PropertyValueFactory("date"));
        date.setPrefWidth(150);
        date.setResizable(false);

        table.getColumns().setAll(header, from, to, date);

        String query;

        if (all) {
            query = "SELECT * FROM notice";
        } else {
            query = "SELECT * FROM notice WHERE sender = " + Staff.ID;
        }

        DatabaseHelper dbh = new DatabaseHelper();
        dbh.launchQuery(query);
        ResultSet cursor = dbh.resultSet;

        try {
            while (cursor.next()) {


                Notice notice = new Notice();
                notice.setHeader(cursor.getString("header"));
                notice.setFrom(cursor.getString("sender"));
                notice.setTo(cursor.getString("receiver"));
                notice.setDate(cursor.getString("date"));
                notice.setId(cursor.getInt("id"));

                rows.add(notice);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbh.close();
        }

        // Container for table
        box = new VBox();
        box.setPadding(new Insets(0, 10, 10, 10));
        box.setSpacing(10);
        box.getChildren().addAll(table);


        mainLayout = new BorderPane(box, null, null, statusBar, null);
        Scene scene = new Scene(mainLayout);
        window.setResizable(false);
        window.getIcons().add(new Image(getClass().getResourceAsStream("Puzzle.png")));
        window.setScene(scene);
        window.sizeToScene();
        window.show();

    }

}
