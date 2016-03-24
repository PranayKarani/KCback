package com.kc.windows; // 04 Mar, 04:06 PM

import com.kc.Utilities.DatabaseHelper;
import com.kc.entity.Student;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.kc.Controllers.MainController.*;

public class AttendanceSheet {

    int       sem;
    String    sub;
    Student[] students;

    // SQL Stings
    String ATTENDANCE_SEM;
    String DATE;
    String SUBJECT_ID;

    // UI stuff
    BorderPane mainLayout;
    VBox       box;
    CheckBox[] checkBoxes;
    Button     submit;
    FlowPane   statusBar;
    Label      status;

    public AttendanceSheet(int sem, int subId, String subName, LocalDate date) throws IOException {

        ATTENDANCE_SEM = "sem_" + sem + "_attendance";
        DATE = "'" + date + "'";
        SUBJECT_ID = "sub_" + subId;

        Stage window = new Stage();
        window.setTitle("Attendance Sheet");
        window.setResizable(false);
        window.getIcons().add(new Image(getClass().getResourceAsStream("Puzzle.png")));
        window.initModality(Modality.APPLICATION_MODAL);

        // Setting up status bar
        status = new Label("Status bar");
        statusBar = new FlowPane(Orientation.HORIZONTAL, status);
        statusBar.setStyle("-fx-background-color: #e8e8e8");
        statusBar.setPadding(new Insets(2, 2, 2, 2));

        // Getting data from attendance database
        ResultSet cursor = DatabaseHelper.launchQuery("SELECT * FROM student WHERE current_sem = " + sem + " ORDER BY roll_no ASC");
        int noofStudents = 0;
        try {

            int cursorSize = 0;
            while (cursor.next()) {
                cursorSize++;
            }

            cursor.beforeFirst();

            System.out.println(cursorSize);
            students = new Student[cursorSize];
            checkBoxes = new CheckBox[cursorSize];
            while (cursor.next()) {
                students[noofStudents] = new Student(cursor);
                checkBoxes[noofStudents] = new CheckBox(students[noofStudents].roll_no + "");
                checkBoxes[noofStudents].setPadding(new Insets(0, 5, 5, 0));
                checkBoxes[noofStudents].setTooltip(new Tooltip(students[noofStudents].name + "\nRoll no: " + students[noofStudents].roll_no));
                // setting up checkbox status
                ResultSet res = DatabaseHelper.launchQuery(
                        "SELECT * FROM " + ATTENDANCE_SEM +
                                " WHERE student_id = " + students[noofStudents].id + " AND date = " + DATE);
                if (res.next()) {
                    if (res.getInt(SUBJECT_ID) == 1) {
                        checkBoxes[noofStudents].setSelected(true);
                    }
                }

                noofStudents++;

            }
            setStatus(noofStudents + " of entries recieved from database", OK);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Setting up checkboxes in grid form
         /*
         final rows = 20
         noof columns = noofStudents / 20
         actual colums = (noof columns) * 2 bcoz, rollno-check box combination
         */
        int gridRows = 2;
        int gridColumns = 2;
        GridPane grid = new GridPane();
        grid.setHgap(10);
        for (int y = 0; y < gridRows; y++) {
            for (int x = 0; x < gridColumns; x++) {

                int idx = (y + 1) + (x * gridRows);

                if (idx > noofStudents) {
                    break;
                }

                grid.add(checkBoxes[idx - 1], x, y);

            }
        }

        // Setting up submit button
        Button button = new Button("Submit");
        button.setOnAction(submitButtonProcessing());


        // Container for checkBoxes and submit button
        box = new VBox();
        box.setPadding(new Insets(0, 10, 10, 10));
        box.setSpacing(10);
        box.getChildren().addAll(grid, button);

        // Container for box and status bar

        TextFlow flow = new TextFlow();

        Text text1 = new Text(subName + " ");
        text1.setStyle("-fx-font-weight: bold; -fx-font-size: 16");

        Text text2 = new Text("(semester " + sem + ") held on ");
        text2.setStyle("-fx-font-weight: normal; -fx-font-size: 14");

        Text text3 = new Text("" + date);
        text3.setStyle("-fx-font-weight: bold; -fx-font-size: 16");

        flow.getChildren().addAll(text1, text2, text3);

//        Label info = new Label("Attendance sheet for "+subName+" (" + sem + " semester) held on " + date);

        flow.setPadding(new Insets(2, 10, 5, 10));
        mainLayout = new BorderPane(box, flow, null, statusBar, null);

        Scene scene = new Scene(mainLayout);
        window.sizeToScene();
        window.setResizable(false);
        window.setScene(scene);
        window.show();
    }

    private EventHandler<ActionEvent> submitButtonProcessing() {

        return event -> {

            int insertCount = 0;
            int updateCount = 0;

            for (int i = 0; i < checkBoxes.length; i++) {

                int present = 0;
                if (checkBoxes[i].isSelected()) {
                    System.out.println("roll no. " + (i + 1) + " present");
                    present = 1;
                } else {
                    System.out.println("roll no. " + (i + 1) + " absent");
                }

                ResultSet result = DatabaseHelper.launchQuery(
                        "SELECT * FROM " + ATTENDANCE_SEM +
                                " WHERE student_id = " + students[i].id + " AND date = " + DATE);
                try {
                    // if result is present get the first one, else insert new
                    if (result.first()) {
                        // update
                        DatabaseHelper.launchUpdate(
                                "UPDATE " + ATTENDANCE_SEM +
                                        " SET " + SUBJECT_ID + " = " + present +
                                        " WHERE date = " + DATE + " AND student_id = " + students[i].id,
                                true);
                        updateCount++;
                    } else {
                        // insert
                        DatabaseHelper.launchUpdate(
                                "INSERT INTO " + ATTENDANCE_SEM + "(student_id,date, " + SUBJECT_ID + ")" +
                                        "VALUES (" + students[i].id + "," + DATE + "," + present + ")",
                                false);
                        insertCount++;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            setStatus(insertCount + " entries inserted, " + updateCount + " updated", SUCCESS);
        };

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
