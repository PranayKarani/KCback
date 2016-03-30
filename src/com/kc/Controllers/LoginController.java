package com.kc.Controllers; // 24 Mar, 04:38 PM

import com.kc.KC;
import com.kc.Utilities.DatabaseHelper;
import com.kc.entity.Staff;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    //status types
    private static final int OK = 0;
    private static final int ERROR = 1;
    private static final int WARN = 2;
    private static final int SUCCESS = 3;
    public BorderPane root;
    public TextField user_name;
    public PasswordField password;
    public Label statusbar_text;
    public FlowPane statusbar;
    Stage window;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void onLogin(ActionEvent actionEvent) {

        window = (Stage) root.getScene().getWindow();

        String username = "'" + user_name.getText() + "'";
        String password = "'" + this.password.getText() + "'";
        int id = getIDfromUsername(user_name.getText());
        setStatus("Wait a second...", OK);


        new Thread(() -> {
            String query;
            if (id != -1) {
                query = "SELECT * FROM staff " +
                        "WHERE staff_id = " + id + " AND " +
                        "password = " + password;
            } else {
                query = "SELECT * FROM staff " +
                        "WHERE user_name = " + username + " AND " +
                        "password = " + password;
            }

            DatabaseHelper dbh = new DatabaseHelper();
            dbh.launchQuery(query);
            ResultSet result = dbh.resultSet;

            try {
                if (result.first()) {

                    try {
                        Staff.HOD = result.getBoolean("HOD");
                        Staff.TEACHING = result.getBoolean("teaching");
                        Staff.ID = result.getInt("staff_id");
                        Staff.USER_NAME = result.getString("user_name");
                        Staff.NAME = result.getString("name");
                        Staff.LAST_OPEN = result.getString("last_open");
                        Staff.PASSWORD = result.getString("password");
                        Staff.EMAIL = result.getString("email");
                        Platform.runLater(() -> KC.loadApp());


                    } catch (SQLException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> setStatus(e.getLocalizedMessage(), ERROR));
                    }

                } else {
                    Platform.runLater(() -> setStatus("Incorrect entries", ERROR));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();

    }


    public void clearStatus(Event event) {
        setStatus("Enter username or id", OK);
    }

    private int getIDfromUsername(String text) {

        boolean isNumeric = true;

        for (int i = 0; i < text.length(); i++) {
            byte b = text.getBytes()[i];
            isNumeric = b < 58 && b > 47;
        }

        if (isNumeric && !text.equals("")) {
            return Integer.parseInt(text);
        } else {
            return -1;
        }

    }

    void setStatus(String text, int type) {
        switch (type) {
            case ERROR:
                statusbar.setStyle("-fx-background-color: #e892a0");
                statusbar_text.setText("(x_x) " + text);
                break;
            case WARN:
                statusbar.setStyle("-fx-background-color: #E8DB74");
                statusbar_text.setText("(o_O) " + text);
                break;
            case SUCCESS:
                statusbar.setStyle("-fx-background-color: #99EB66");
                statusbar_text.setText("(^_^) " + text);
                break;
            default:
                statusbar.setStyle("-fx-background-color: #E8E8E8");
                statusbar_text.setText(text);
                break;
        }

    }
}
