package com.kc.windows; // 13 Mar, 12:56 PM

import com.kc.Utilities.C;
import com.kc.Utilities.DatabaseHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.kc.Controllers.MainController.*;

/**
 * Executes SQL query on the database and displays results in a popup window
 */
public class SQLResults {

    final int SELECT  = 0;
    final int INSERT  = 1;
    final int UPDATE  = 2;
    final int DELETE  = 3;
    final int INVALID = -1;
    String query;
    int noofRows;
    int noofCols;
    // query type;
    int TYPE;
    // UI stuff
    BorderPane              mainLayout;
    VBox                    box;
    TableView table;
    ObservableList<ObservableList> rows;

    FlowPane statusBar;
    Label    status;

    public SQLResults(String query, int noofRows) {
        this.query = query;
        this.noofRows = noofRows;

        Stage window = new Stage();
        window.setTitle("SQL results");
        window.setResizable(false);
        window.getIcons().add(new Image(getClass().getResourceAsStream("Puzzle.png")));
        window.initModality(Modality.APPLICATION_MODAL);

        // Setting up status bar
        status = new Label("Status bar");
        statusBar = new FlowPane(Orientation.HORIZONTAL, status);
        statusBar.setStyle("-fx-background-color: #e8e8e8");
        statusBar.setPadding(new Insets(2, 2, 2, 2));


        // setting up Table
        table = new TableView<>();
        rows = FXCollections.observableArrayList();
        table.setItems(rows);

        // Container for table
        box = new VBox();
        box.setPadding(new Insets(0, 10, 10, 10));
        box.setSpacing(10);

        TYPE = getQueryType(query);

        ResultSet cursor;
        String result;

        switch (TYPE) {
            case SELECT:
                try {
                    cursor = DatabaseHelper.launchQuery(limitQuery(query));
                    if (cursor == null) {
                        setStatus(C.SQL_RESULT, ERROR);
                        break;
                    }


                    noofCols = cursor.getMetaData().getColumnCount();

                    for (int i = 0; i < noofCols; i++) {

                        TableColumn col = new TableColumn<>(cursor.getMetaData().getColumnName(i + 1));
                        col.setSortable(false);
                        col.setMinWidth(cursor.getMetaData().getColumnName(i + 1).length()*7);
                        final int finalI = i;
                        col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                            @Override
                            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                                if (param.getValue().get(finalI) != null)
                                    return new SimpleStringProperty(param.getValue().get(finalI).toString());
                                else {
                                    return new SimpleObjectProperty<>("-");
                                }
                            }
                        });
                        table.getColumns().add(col);
                    }

                    int rowCount = 0;

                    while (cursor.next()) {


                        String[] elements = new String[noofCols];

                        ObservableList<String> row = FXCollections.observableArrayList();

                        for (int i = 0; i < noofCols; i++) {
                            row.add(cursor.getString(i + 1));
                        }

                        rows.add(row);
                        rowCount++;

                    }

                    box.getChildren().addAll(table);
                    setStatus("Showing " + rowCount + " rows", SUCCESS);

                } catch (SQLException e) {
                    setStatus(e.getLocalizedMessage(), ERROR);
                }

                break;
            case UPDATE:

                result = DatabaseHelper.launchUpdate(query, false);
                if(result.substring(0,3).equals("oxo")) {
                    setStatus("Updated " + result.substring(3) + " rows", SUCCESS);
                } else {
                    setStatus(result, ERROR);
                }

                break;
            case INSERT:

                result = DatabaseHelper.launchUpdate(query, false);
                if(result.substring(0,3).equals("oxo")) {
                    setStatus("Inserted " + result.substring(3) + " rows", SUCCESS);
                } else {
                    setStatus(result, ERROR);
                }

                break;
            case DELETE:

                result = DatabaseHelper.launchUpdate(query, false);
                if(result.substring(0,3).equals("oxo")) {
                    setStatus("Deleted " + result.substring(3) + " rows", SUCCESS);
                } else {
                    setStatus(result, ERROR);
                }

                break;
            default:
                setStatus("only SELECT, UPDATE, INSERT, DELETE supported", ERROR);
                break;
        }

        Label label = new Label(query.replaceAll("\n",""));
        label.setWrapText(true);
        label.setPadding(new Insets(2,5,2,5));
        label.setStyle("-fx-background-color: #CEE0EF");
        HBox queryBox = new HBox(label);
        queryBox.setPadding(new Insets(10, 10, 10, 10));

        mainLayout = new BorderPane(box, queryBox, null, statusBar, null);

        Scene scene = new Scene(mainLayout);
        window.setResizable(true);
        window.getIcons().add(new Image(getClass().getResourceAsStream("Puzzle.png")));
        window.setScene(scene);
        window.sizeToScene();
        window.show();
    }

    int getQueryType(String q) {
        switch (q.charAt(0)) {
            case 'S':
                return SELECT;
            case 's':
                return SELECT;
            case 'I':
                return INSERT;
            case 'i':
                return INSERT;
            case 'U':
                return UPDATE;
            case 'u':
                return UPDATE;
            case 'd':
                return DELETE;
            case 'D':
                return DELETE;
            default:
                return INVALID;
        }
    }

    String limitQuery(String q) {

        return q + " LIMIT " + noofRows;
    }

    void setStatus(String text, int type) {

        switch (type) {
            case ERROR:
                statusBar.setStyle("-fx-background-color: #e892a0");
                status.setText("(x_x) " + text);
                break;
            case WARN:
                statusBar.setStyle("-fx-background-color: #E8DB74");
                status.setText("(o_O) " + text);
                break;
            case SUCCESS:
                statusBar.setStyle("-fx-background-color: #99EB66");
                status.setText("(^_^) " + text);
                break;
            default:
                statusBar.setStyle("-fx-background-color: #E8E8E8");
                status.setText(text);
                break;
        }

    }



}
