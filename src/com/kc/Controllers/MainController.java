package com.kc.Controllers; // 04 Mar, 05:40 PM

import com.kc.Utilities.C;
import com.kc.Utilities.DatabaseHelper;
import com.kc.Utilities.RemoteDatabaseConnecter;
import com.kc.entity.Staff;
import com.kc.windows.AttendanceSheet;
import com.kc.windows.NoticeHistory;
import com.kc.windows.SQLResults;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    //status types
    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int WARN = 2;
    public static final int SUCCESS = 3;
    final String ALL = "all students";
    final String FY = "F.Y.BSc. I.T. students";
    final String SY = "S.Y. BSc. I.T. students";
    final String TY = "T.Y. BSc. I.T. students";
    final String MON = "Monday";
    final String TUE = "Tuesday";
    final String WED = "Wednesday";
    final String THR = "Thursday";
    final String FRI = "Friday";


    // Time Table Stuff
    final String SAT = "Saturday";
    final String AM = "am";
    final String PM = "pm";
    final int tt_maxLength = 2;
    // Send Notice Stuff
    public ChoiceBox<String> to_choice;
    public Label from_text;
    public TextField header_text;
    public TextArea notice_text;
    public Button sendNotice_button;
    // Attendance Stuff
    public ChoiceBox<Integer> as_sem_choice;
    public String[] as_subject_names;
    public ChoiceBox<String> as_sub_choice;
    public DatePicker as_datePicker;
    public ChoiceBox<Integer> tt_sem_choice;
    public String[] tt_subject_names;
    public ChoiceBox<String> tt_sub_choice;
    public ChoiceBox<String> tt_day_choice;
    public ChoiceBox<String> tt_start_ampm_choice;
    public ChoiceBox<String> tt_end_ampm_choice;
    public TextField tt_teacher;
    public TextField tt_start_hour;
    public TextField tt_start_min;
    public TextField tt_end_hour;
    public TextField tt_end_min;
    // SQL stuff
    public TextArea sql_query_text;
    public ChoiceBox<Integer> resultRows_choice;
    // Student insert stuff
    public TextField si_id;
    public TextField si_name;
    public TextField si_roll_no;
    public CheckBox si_active;
    public ChoiceBox<Integer> si_sem;
    public TextField si_email;
    public Button si_submit;
    public Button si_delete;
    // Staff stuff
    public TextField stf_id;
    public TextField stf_name;
    public TextField stf_username;
    public TextField stf_password;
    public TextField stf_email;
    public Label stf_lastopen;
    public Button stf_submit;
    public Button stf_delete;
    public CheckBox stf_teaching;
    public CheckBox stf_HOD;

    // Common Stuff
    public Label statusbar_text;
    public FlowPane statusbar;
    private boolean studentPresent = false;
    private boolean staffPresent = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String last_open = "You last login was at " + (Staff.LAST_OPEN == null ? "-" : Staff.LAST_OPEN);
        setStatus(last_open, WARN);
        String timestamp = "'" + LocalDateTime.now() + "'";
        String resultSet = DatabaseHelper.launchUpdate("UPDATE staff SET last_open = " + timestamp + " WHERE staff_id = " + Staff.ID, true);

        // Notice stuff
        from_text.setText("From: " + Staff.NAME);
        to_choice.getItems().addAll(ALL, FY, SY, TY);
        to_choice.setValue(ALL);
        notice_text.setWrapText(true);

        // Attendance Sheet

        as_sem_choice.setDisable(Staff.HOD);
        as_sem_choice.getItems().setAll(1, 2, 3, 4, 5, 6);
        as_sem_choice.getSelectionModel().selectedItemProperty()
                .addListener(on_as_sem_change());
//        as_subject_names = new String[5];
        as_datePicker.setValue(LocalDate.now());


        // Time Table

        tt_sem_choice.getItems().setAll(1, 2, 3, 4, 5, 6);
        tt_sem_choice.getSelectionModel().selectedItemProperty()
                .addListener(on_tt_sem_change());
//        tt_subject_names = new String[5];
//        tt_subject_teachers = new String[5];
        tt_teacher.setDisable(!Staff.HOD);
        tt_sub_choice.getSelectionModel().selectedItemProperty()
                .addListener(on_tt_sub_change());
        tt_day_choice.getItems().setAll(MON, TUE, WED, THR, FRI, SAT);
        tt_day_choice.setValue(MON);
        tt_day_choice.getSelectionModel().selectedItemProperty()
                .addListener(on_tt_day_change());
        // start time
        tt_start_ampm_choice.getItems().setAll(AM, PM);
        tt_start_ampm_choice.setValue(AM);
        tt_start_hour.textProperty().addListener(textLimiter(tt_maxLength, tt_start_hour));
        tt_start_min.textProperty().addListener(textLimiter(tt_maxLength, tt_start_min));
        // endable only if HOD
        tt_start_hour.setDisable(!Staff.HOD);
        tt_start_min.setDisable(!Staff.HOD);
        //  end time
        tt_end_ampm_choice.getItems().setAll(AM, PM);
        tt_end_ampm_choice.setValue(AM);
        tt_end_hour.textProperty().addListener(textLimiter(tt_maxLength, tt_end_hour));
        tt_end_min.textProperty().addListener(textLimiter(tt_maxLength, tt_end_min));
        // enable only if HOD
        tt_end_hour.setDisable(!Staff.HOD);
        tt_end_min.setDisable(!Staff.HOD);

        // Student insert stuff
        si_id.textProperty().addListener(on_si_id_change());
        si_sem.getItems().setAll(1, 2, 3, 4, 5, 6);
        si_sem.setValue(1);
        si_active.setSelected(false);
        si_submit.setText("insert");
        si_delete.setDisable(true);

        // Staff stuff
        stf_id.setText("" + Staff.ID);
        stf_id.textProperty().addListener(on_stf_id_change());
        stf_id.setDisable(!Staff.HOD);
        stf_name.setText(Staff.NAME);
        stf_name.setDisable(!Staff.HOD);
        stf_username.setText(Staff.USER_NAME);
        stf_password.setText(Staff.PASSWORD);
        stf_email.setText(Staff.EMAIL);
        stf_lastopen.setText(Staff.LAST_OPEN);
        stf_submit.setText(Staff.HOD ? "insert" : "update");
        stf_delete.setDisable(!Staff.HOD);
        stf_teaching.setSelected(Staff.TEACHING);
        stf_teaching.setDisable(!Staff.HOD);
        stf_HOD.setSelected(Staff.HOD);
        stf_HOD.setDisable(!Staff.HOD);

        // SQL stuff
        resultRows_choice.getItems().setAll(5, 10, 15, 20, 25, 50);
        resultRows_choice.setValue(10);


        statusbar_text.setMaxWidth(600);
        statusbar_text.setWrapText(true);

    }

    /**
     * Send Notice tab
     */

    public void onSendNoticeClick() throws IOException {

        if (from_text.getText().isEmpty()) {
            setStatus("\"From\" field cannot be empty", WARN);
        } else if (header_text.getText().isEmpty()) {
            setStatus("Notice header cannot be empty", WARN);
        } else if (notice_text.getText().isEmpty()) {
            setStatus("Notice body cannot be empty", WARN);
        } else {

            int from = Staff.ID;
            String header = header_text.getText();
            String notice = notice_text.getText();
            int to = to_choice.getItems().indexOf(to_choice.getValue());

            // todo thread this operation
            RemoteDatabaseConnecter rdc = new RemoteDatabaseConnecter("POST", C.SEND_NOTICE)
                    .connect("from=" + from + "&header=" + header + "&to=" + to + "&message=" + notice);
            System.out.println(rdc.rawData);

            setStatus("Notice sent succesfully!", SUCCESS);
        }

    }

    public void viewNoticeHistory() throws IOException {

        new NoticeHistory(true);

    }

    public void viewNoticeMyHistory() throws IOException {

        new NoticeHistory(false);

    }

    /**
     * Attendance tab
     */

    ChangeListener on_as_sem_change() {
        return (observable, oldValue, newValue) -> {

            int selectedSem = as_sem_choice.getValue();

            // get subjects for selected Sem from database
            ResultSet result = DatabaseHelper.launchQuery("SELECT * FROM subject WHERE sem = " + newValue + " AND staff_id = " + Staff.ID);
            int i = 0;
            try {

                int x = 0;
                while (result.next()) {
                    x++;
                }
                as_subject_names = new String[x];
                System.out.println("Fethc: " + as_subject_names.length);

                result.beforeFirst();
                while (result.next()) {
                    as_subject_names[i] = result.getString("full_name");
                    i++;
                }

                // fill choicebox with subjects and set default
                as_sub_choice.getItems().setAll(as_subject_names);
                if (as_subject_names.length > 0) {
                    as_sub_choice.setValue(as_subject_names[0]);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        };
    }

    public void onGetSheetClick() throws IOException {

        int selectedSem;
        String selectedSubject;

        if (as_sem_choice.getValue() == null) {
            setStatus("Enter valid semeseter", WARN);

        } else {

            selectedSem = as_sem_choice.getValue();

            if (as_sub_choice.getValue() == null) {
                setStatus("Select required subject", WARN);
            } else {

                selectedSubject = as_sub_choice.getValue();

                int selectedSubIndex = as_sub_choice.getItems().indexOf(selectedSubject);
                int selectedSubID = ((selectedSem - 1) * 5) + (selectedSubIndex + 1);
                LocalDate localDate = as_datePicker.getValue();
                new AttendanceSheet(selectedSem, selectedSubID, selectedSubject, localDate);
                setStatus("", OK);
            }
        }

    }

    /**
     * Time Table tab
     */

    ChangeListener on_tt_sem_change() {
        return (observable, oldValue, newValue) -> {

            int selectedSem = tt_sem_choice.getValue();

            // get subjects for selected Sem from database
            ResultSet result = DatabaseHelper.launchQuery("SELECT * FROM subject WHERE sem = " + newValue + " AND staff_id = " + Staff.ID);
            int x = 0;
            try {
                while (result.next()) {
                    x++;
                }

                tt_subject_names = new String[x];

                result.beforeFirst();

                for (int i = 0; result.next(); i++) {

                    tt_subject_names[i] = result.getString("full_name");
//                    tt_subject_teachers[i] = result.getString("staff_id");
                    i++;
                }

            } catch (SQLException e) {
                setStatus(e.getLocalizedMessage(), ERROR);
            }
            // fill choicebox with subjects and set default
            tt_sub_choice.getItems().setAll(tt_subject_names);
            if (tt_subject_names.length > 0) {
                tt_sub_choice.setValue(tt_subject_names[0]);
            }
            // get selected subject index in choicebox and generate subject_id
            int selectedSubIndex = tt_sub_choice.getItems().indexOf(tt_sub_choice.getValue());
            int selectedSubID = ((selectedSem - 1) * 5) + (selectedSubIndex + 1);

            // get select day
            int day = getDayId(tt_day_choice.getValue());

            // show subject teacher
            tt_teacher.setText(Staff.NAME);

            // setting up time fields but first clear those fields
            tt_start_hour.clear();
            tt_start_min.clear();
            tt_end_hour.clear();
            tt_end_min.clear();
            // query timings from database
            ResultSet time_result = DatabaseHelper.launchQuery("SELECT * FROM timetable WHERE sub_ID = " + selectedSubID + " AND day_of_week = " + day);
            LocalTime startTime, endTime;
            try {
                while (time_result.next()) {
                    StringBuilder timeBuilder = new StringBuilder();

                    startTime = time_result.getTime("start_time").toLocalTime();
                    endTime = time_result.getTime("end_time").toLocalTime();

                    // fill start time fields
                    tt_end_ampm_choice.setValue(startTime.getHour() > 12 ? PM : AM);
                    timeBuilder.append(startTime.getHour() > 12 ? (startTime.getHour() - 12) : startTime.getHour());
                    tt_start_hour.setText(timeBuilder.toString());
                    timeBuilder.delete(0, timeBuilder.length()).append(startTime.getMinute());
                    tt_start_min.setText(timeBuilder.toString());

                    // reset string builder
                    timeBuilder.delete(0, timeBuilder.length());

                    // fill end time fields
                    tt_end_ampm_choice.setValue(endTime.getHour() > 12 ? PM : AM);
                    timeBuilder.append(endTime.getHour() > 12 ? (endTime.getHour() - 12) : endTime.getHour());
                    tt_end_hour.setText(timeBuilder.toString());
                    timeBuilder.delete(0, timeBuilder.length()).append(endTime.getMinute());
                    tt_end_min.setText(timeBuilder.toString());

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        };
    }

    ChangeListener on_tt_sub_change() {
        return (observable, oldValue, newValue) -> {

            int selectedSem = tt_sem_choice.getValue();

            // get selected subject index in choicebox and generate subject_id
            int selectedSubIndex = tt_sub_choice.getItems().indexOf(tt_sub_choice.getValue());
            int selectedSubID = ((selectedSem - 1) * 5) + (selectedSubIndex + 1);

            // get seelect day
            int day = getDayId(tt_day_choice.getValue());

            // show subject teacher
            tt_teacher.setText(Staff.NAME);

            // setting up time fields but first clear those fields
            tt_start_hour.clear();
            tt_start_min.clear();
            tt_end_hour.clear();
            tt_end_min.clear();
            // query timings from database
            ResultSet time_result = DatabaseHelper.launchQuery("SELECT * FROM timetable WHERE sub_ID = " + selectedSubID + " AND day_of_week = " + day);
            LocalTime startTime, endTime;
            try {
                while (time_result.next()) {
                    StringBuilder timeBuilder = new StringBuilder();

                    startTime = time_result.getTime("start_time").toLocalTime();
                    endTime = time_result.getTime("end_time").toLocalTime();

                    // fill start time fields
                    tt_end_ampm_choice.setValue(startTime.getHour() > 12 ? PM : AM);
                    timeBuilder.append(startTime.getHour() > 12 ? (startTime.getHour() - 12) : startTime.getHour());
                    tt_start_hour.setText(timeBuilder.toString());
                    timeBuilder.delete(0, timeBuilder.length()).append(startTime.getMinute());
                    tt_start_min.setText(timeBuilder.toString());

                    // reset string builder
                    timeBuilder.delete(0, timeBuilder.length());

                    // fill end time fields
                    tt_end_ampm_choice.setValue(endTime.getHour() > 12 ? PM : AM);
                    timeBuilder.append(endTime.getHour() > 12 ? (endTime.getHour() - 12) : endTime.getHour());
                    tt_end_hour.setText(timeBuilder.toString());
                    timeBuilder.delete(0, timeBuilder.length()).append(endTime.getMinute());
                    tt_end_min.setText(timeBuilder.toString());

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

    ChangeListener on_tt_day_change() {
        return (observable, oldValue, newValue) -> {

            int selectedSem = tt_sem_choice.getValue();

            // get selected subject index in choicebox and generate subject_id
            int selectedSubIndex = tt_sub_choice.getItems().indexOf(tt_sub_choice.getValue());
            int selectedSubID = ((selectedSem - 1) * 5) + (selectedSubIndex + 1);

            // get seelect day
            int day = getDayId(tt_day_choice.getValue());

            // setting up time fields but first clear those fields
            tt_start_hour.clear();
            tt_start_min.clear();
            tt_end_hour.clear();
            tt_end_min.clear();
            // query timings from database
            ResultSet time_result = DatabaseHelper.launchQuery("SELECT * FROM timetable WHERE sub_ID = " + selectedSubID + " AND day_of_week = " + day);
            LocalTime startTime, endTime;
            try {
                while (time_result.next()) {
                    StringBuilder timeBuilder = new StringBuilder();

                    startTime = time_result.getTime("start_time").toLocalTime();
                    endTime = time_result.getTime("end_time").toLocalTime();

                    // fill start time fields
                    tt_end_ampm_choice.setValue(startTime.getHour() > 12 ? PM : AM);
                    timeBuilder.append(startTime.getHour() > 12 ? (startTime.getHour() - 12) : startTime.getHour());
                    tt_start_hour.setText(timeBuilder.toString());
                    timeBuilder.delete(0, timeBuilder.length()).append(startTime.getMinute());
                    tt_start_min.setText(timeBuilder.toString());

                    // reset string builder
                    timeBuilder.delete(0, timeBuilder.length());

                    // fill end time fields
                    tt_end_ampm_choice.setValue(endTime.getHour() > 12 ? PM : AM);
                    timeBuilder.append(endTime.getHour() > 12 ? (endTime.getHour() - 12) : endTime.getHour());
                    tt_end_hour.setText(timeBuilder.toString());
                    timeBuilder.delete(0, timeBuilder.length()).append(endTime.getMinute());
                    tt_end_min.setText(timeBuilder.toString());

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

    public void onTTSubmit() throws IOException {

        int selectedSem;
        String selectedSubject;

        int startTime_h = isAllNumbers(tt_start_hour.getText()) ? Integer.parseInt(tt_start_hour.getText()) : 0;
        int startTime_m = isAllNumbers(tt_start_min.getText()) ? Integer.parseInt(tt_start_min.getText()) : 0;
        int endTime_h = isAllNumbers(tt_end_hour.getText()) ? Integer.parseInt(tt_end_hour.getText()) : 0;
        int endTime_m = isAllNumbers(tt_end_min.getText()) ? Integer.parseInt(tt_end_min.getText()) : 0;


        // sem checking
        if (tt_sem_choice.getValue() == null) {
            setStatus("Enter valid semester", WARN);

            // hour should not be empty
        } else if (tt_start_hour.getText() == null || tt_start_hour.getText().isEmpty()) {
            setStatus("Enter Starting hour", WARN);

            // hour should be numbers
        } else if (!isAllNumbers(tt_start_hour.getText())) {
            setStatus("Enter time in numbers", WARN);

            // hour should be <= 12
        } else if (startTime_h > 12 || startTime_h < 1) {
            setStatus("Enter time 12 hour format", WARN);


            // min should not be empty
        } else if (tt_start_min.getText() == null || tt_start_min.getText().isEmpty()) {
            setStatus("Enter Starting min", WARN);

            // min should be numbers
        } else if (!isAllNumbers(tt_start_min.getText())) {
            setStatus("Enter time in numbers", WARN);

            // min should be <= 59
        } else if (startTime_m > 59 || startTime_m < 0) {
            setStatus("Enter time 12 hour format", WARN);


            // Ending hour
        } else if (tt_end_hour.getText() == null || tt_end_hour.getText().isEmpty()) {
            setStatus("Enter Ending hour", WARN);

            // hour should be numbers
        } else if (!isAllNumbers(tt_end_hour.getText())) {
            setStatus("Enter time in numbers", WARN);

            // hour should be <= 12
        } else if (endTime_h > 12 || endTime_h < 1) {
            setStatus("Enter time 12 hour format", WARN);


            // min should not be empty
        } else if (tt_end_min.getText() == null || tt_end_min.getText().isEmpty()) {
            setStatus("Enter Ending min", WARN);

            // min should be numbers
        } else if (!isAllNumbers(tt_end_min.getText())) {
            setStatus("Enter time in numbers", WARN);

            // min should be <= 59
        } else if (endTime_m > 59 || endTime_m < 0) {
            setStatus("Enter time 12 hour format", WARN);


            // DONE!!!
        } else {

            selectedSem = tt_sem_choice.getValue();
            selectedSubject = tt_sub_choice.getValue();

            int selectedSubIndex = tt_sub_choice.getItems().indexOf(selectedSubject);
            int selectedSubID = ((selectedSem - 1) * 5) + (selectedSubIndex + 1);

            int dayID = getDayId(tt_day_choice.getValue());

            String insertTeacher = "'" + tt_teacher.getText() + "'";

            System.out.println(
                    "Sem: " + selectedSem + "\n" +
                            "Subject: " + selectedSubject + "\n" +
                            "Subject ID: " + selectedSubIndex + "\n" +
                            "Day: " + tt_day_choice.getValue() + "\n" +
                            "Teacher: " + tt_teacher.getText() + "\n" +
                            "Start Time: " + startTime_h + ":" + startTime_m + " " + tt_start_ampm_choice.getValue() + "\n" +
                            "End Time: " + endTime_h + ":" + endTime_m + " " + tt_end_ampm_choice.getValue() + "\n" +
                            "Formatted starttime: " + timeFormat_24(startTime_h, startTime_m, tt_start_ampm_choice.getValue()) + "\n" +
                            "Formatted endtime: " + timeFormat_24(endTime_h, endTime_m, tt_end_ampm_choice.getValue()) + "\n"

            );

            String startTimeForQuery = "'" + timeFormat_24(startTime_h, startTime_m, tt_start_ampm_choice.getValue()) + "'";
            String endTimeForQuery = "'" + timeFormat_24(endTime_h, endTime_m, tt_end_ampm_choice.getValue()) + "'";

            ResultSet result = DatabaseHelper
                    .launchQuery("SELECT * FROM timetable WHERE sub_ID = " + selectedSubID + " AND day_of_week = " + dayID);
            String statusString = "";
            boolean err = false;
            try {
                // if result is present get the first one, else insert new
                if (result.first()) {
                    // update
                    String r = DatabaseHelper.launchUpdate(
                            "UPDATE timetable " +
                                    " SET start_time = " + startTimeForQuery + ", end_time = " + endTimeForQuery + ", teacher = " + insertTeacher +
                                    " WHERE sub_ID = " + selectedSubID + " AND day_of_week = " + dayID,
                            true);

                    if (r.substring(0, 3).equals("oxo")) {
                        statusString =
                                "timings for " + selectedSubject + " updated, " +
                                        startTime_h + ":" + startTime_m + " " + tt_start_ampm_choice.getValue() + " to " +
                                        endTime_h + ":" + endTime_m + " " + tt_end_ampm_choice.getValue() + " " +
                                        "on " + tt_day_choice.getValue();
                    } else {
                        statusString = r;
                        err = true;
                    }

                } else {
                    // insert
                    String r = DatabaseHelper.launchUpdate(
                            "INSERT INTO timetable(sub_ID, day_of_week, start_time, end_time, teacher)" +
                                    "VALUES (" + selectedSubID + "," + dayID + "," + startTimeForQuery + "," + endTimeForQuery + "," + insertTeacher + ")",
                            false);

                    if (r.substring(0, 3).equals("oxo")) {
                        statusString =
                                "timings for " + selectedSubject + " inserted, " +
                                        startTime_h + ":" + startTime_m + " " + tt_start_ampm_choice.getValue() + " to " +
                                        endTime_h + ":" + endTime_m + " " + tt_end_ampm_choice.getValue() + " " +
                                        "on " + tt_day_choice.getValue();
                    } else {
                        statusString = r;
                        err = true;
                    }
                }

            } catch (SQLException e) {
                setStatus(e.getLocalizedMessage(), ERROR);
            }

            setStatus(statusString, err ? ERROR : SUCCESS);

        }
    }

    /**
     * SQL query tab
     */
    public void onQueryExecute() {
        String query = sql_query_text.getText();

        if (query.equals("") || query == null) {
            setStatus("Blank query", ERROR);
        } else {
            new SQLResults(query, resultRows_choice.getValue());
            System.out.println(query);
        }

    }

    /**
     * Student Insertion stuff
     */

    ChangeListener on_si_id_change() {
        return (observable, oldValue, newValue) -> {

            String id = si_id.getText();

            if (id.length() > 3) {

                ResultSet result = DatabaseHelper.launchQuery("SELECT * FROM student WHERE student_id = " + id);
                try {
                    if (result.first()) {
                        si_name.setText(result.getString("name"));
                        si_roll_no.setText("" + result.getInt("roll_no"));
                        si_sem.setValue(result.getInt("current_sem"));
                        si_active.setSelected(result.getInt("active") == 1);
                        si_email.setText(result.getString("email"));
                        studentPresent = true;
                        si_delete.setDisable(false);
                        si_submit.setText("update");
                        setStatus("student found!", OK);
                    } else {
                        si_name.clear();
                        si_roll_no.clear();
                        si_active.setSelected(false);
                        si_email.clear();
                        studentPresent = false;
                        si_delete.setDisable(true);
                        si_submit.setText("insert");
                        setStatus("no student found", OK);
                    }
                } catch (SQLException e) {
                    DatabaseHelper.close();
                    setStatus(e.getLocalizedMessage(), ERROR);
                }

            } else {
                studentPresent = false;// i don't why, but ...
                setStatus("Keep typing...", OK);
            }


        };
    }

    public void onSISubmit(ActionEvent actionEvent) {

        String ID = si_id.getText();
        int ACTIVE = si_active.isSelected() ? 1 : 0;
        int SEM = si_sem.getValue();
        String ROLL_NO = si_roll_no.getText();
        String NAME = "'" + si_name.getText() + "'";
        String EMAIL = "'" + si_email.getText() + "'";

        String query;
        if (studentPresent) {
            query = "UPDATE student " +
                    "SET active = " + ACTIVE + "," +
                    "current_sem = " + SEM + "," +
                    "roll_no = " + ROLL_NO + "," +
                    "name = " + NAME + "," +
                    "email = " + EMAIL + " " +
                    "WHERE student_id = " + ID;
        } else {
            query = "INSERT INTO student(student_id, active, current_sem, roll_no, name, email) " +
                    "VALUES (" + ID + "," + ACTIVE + "," + SEM + "," + ROLL_NO + "," + NAME + "," + EMAIL + ")";
        }

        String statusString = DatabaseHelper.launchUpdate(query, studentPresent);
        boolean err = false;

        if (statusString.substring(0, 3).equals("oxo")) {
            if (studentPresent) {
                statusString = "updated";
            } else {
                statusString = "inserted";
                studentPresent = true;
                si_delete.setDisable(false);
            }
        } else {
            err = true;
        }

        setStatus(statusString, err ? ERROR : SUCCESS);
        System.out.println(studentPresent ? "update" : "insert");

    }

    public void onSIdelete(ActionEvent actionEvent) {

        String statusString = DatabaseHelper.launchUpdate("DELETE FROM student WHERE student_id = " + si_id.getText(), false);
        boolean err = false;
        if (statusString.substring(0, 3).equals("oxo")) {
            statusString = "Deleted!\nPress insert if deleted by mistake. NOTE: foreign key references can't be restored";
            si_submit.setText("insert");
            studentPresent = false;
            si_delete.setDisable(true);
        } else {
            err = true;
        }
        setStatus(statusString, err ? ERROR : SUCCESS);

    }

    /**
     * Staff Stuff
     */
    ChangeListener on_stf_id_change() {
        return (observable, oldValue, newValue) -> {

            String id = stf_id.getText();

            if (id.length() > 2) {

                ResultSet result = DatabaseHelper.launchQuery("SELECT * FROM staff WHERE staff_id = " + id);
                try {
                    if (result.first()) {
                        stf_name.setText(result.getString("name"));
                        stf_username.setText(result.getString("user_name"));
                        stf_password.setText(result.getString("password"));
                        stf_email.setText(result.getString("email"));
                        staffPresent = true;
                        stf_delete.setDisable(false);
                        stf_submit.setText("update");
                        setStatus("Staff found!", OK);
                    } else {
                        stf_name.clear();
                        stf_username.clear();
                        stf_password.clear();
                        stf_email.clear();
                        staffPresent = false;
                        stf_delete.setDisable(true);
                        stf_submit.setText("insert");
                        setStatus("no Staff found", OK);
                    }
                } catch (SQLException e) {
                    DatabaseHelper.close();
                    setStatus(e.getLocalizedMessage(), ERROR);
                }

            } else {
                staffPresent = false;// i don't why, but ...
                setStatus("Keep typing...", OK);
            }


        };
    }

    public void onStfSubmit(ActionEvent actionEvent) {
        String ID = stf_id.getText();
        int TEACHING = stf_teaching.isSelected() ? 1 : 0;
        int HOD = stf_HOD.isSelected() ? 1 : 0;
        String NAME = "'" + stf_name.getText() + "'";
        String USER_NAME = "'" + stf_username.getText() + "'";
        String PASSWORD = "'" + stf_password.getText() + "'";
        String EMAIL = "'" + stf_email.getText() + "'";

        String query;
        if (staffPresent) {
            query = "UPDATE staff " +
                    "SET teaching = " + TEACHING + "," +
                    "HOD = " + HOD + "," +
                    "name = " + NAME + "," +
                    "user_name = " + USER_NAME + "," +
                    "password = " + PASSWORD + "," +
                    "email = " + EMAIL + " " +
                    "WHERE staff_id = " + ID;
        } else {
            query = "INSERT INTO staff(staff_id, name, user_name, password, teaching, HOD, email) " +
                    "VALUES (" + ID + "," + NAME + "," + USER_NAME + "," + PASSWORD + "," + TEACHING + "," + HOD + "," + EMAIL + ")";
        }

        String statusString = DatabaseHelper.launchUpdate(query, staffPresent);
        boolean err = false;

        if (statusString.substring(0, 3).equals("oxo")) {
            if (staffPresent) {
                statusString = "updated";
            } else {
                statusString = "inserted";
                staffPresent = true;
                si_delete.setDisable(false);
            }
        } else {
            err = true;
        }

        setStatus(statusString, err ? ERROR : SUCCESS);
        System.out.println(staffPresent ? "update" : "insert");
    }

    public void onStfdelete(ActionEvent actionEvent) {
        String statusString = DatabaseHelper.launchUpdate("DELETE FROM staff WHERE staff_id = " + si_id.getText(), false);
        boolean err = false;
        if (statusString.substring(0, 3).equals("oxo")) {
            statusString = "Deleted!\nPress insert if deleted by mistake. NOTE: foreign key references can't be restored";
            si_submit.setText("insert");
            staffPresent = false;
            si_delete.setDisable(true);
        } else {
            err = true;
        }
        setStatus(statusString, err ? ERROR : SUCCESS);
    }

    /**
     * Common stuff
     */

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

    ChangeListener<String> textLimiter(int limit, final TextField field) {
        return (observable, oldValue, newValue) -> {

            boolean ignore = false;

            if (ignore || newValue == null) {
                return;
            }

            if (newValue.length() > limit) {
                ignore = true;
                field.setText(newValue.substring(0, limit));

            }

        };
    }

    boolean isAllNumbers(String s) {
        boolean isNumbers = false;
        for (int i = 0; i < s.length(); i++) {
            byte b = s.getBytes()[i];
            isNumbers = b < 58 && b > 47;
        }
        return isNumbers;
    }

    int getDayId(String day) {
        switch (day) {
            case MON:
                return 1;
            case TUE:
                return 2;
            case WED:
                return 3;
            case THR:
                return 4;
            case FRI:
                return 5;
            case SAT:
                return 6;
            default:
                return 0;
        }
    }

    String timeFormat_24(int h, int m, String ampm) {
        StringBuilder time = new StringBuilder(6);
        if (ampm.equals(AM)) {
            h = h == 12 ? 0 : h;
        } else {
            h = h == 12 ? 12 : 12 + h;
        }
        time.append(h < 10 ? "0" + h : h).append(m < 10 ? "0" + m : m).append("00");
        return time.toString();
    }

}
