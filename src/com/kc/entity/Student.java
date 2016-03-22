package com.kc.entity; // 12 Mar, 07:48 PM

import java.sql.ResultSet;
import java.sql.SQLException;

public class Student {

    public int id;
    public String name;
    public int roll_no;
    public boolean present;// for attendance sheet purpose

    public Student(ResultSet cursor) throws SQLException {
        this.roll_no = cursor.getInt("roll_no");
        this.id = cursor.getInt("student_id");
        this.name = cursor.getString("name");
    }
}
