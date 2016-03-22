package com.kc.Utilities; // 12 Mar, 07:18 PM

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class that helps in direct database access instead using php
 */

public class DatabaseHelper {

    /**
     * <p>Ask a query and get the result in the form of cursor.</p>
     * @param query
     * @return
     * @throws SQLException
     */
    public static ResultSet launchQuery(final String query){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kc", "Pranay", "");
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * For Update and insert queries
     * @param query
     * @param update - update or insert?
     * @return
     */
    public static String launchUpdate(final String query, boolean update) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kc", "Pranay", "");
            Statement statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            if(update){
                if(query.charAt(0) == 'd' || query.charAt(0) == 'D'){
                    return "oxo" + i;
                } else {
                    return "oxo" + i;
                }
            } else {
                return "oxo" + i;
            }
        } catch (SQLException e) {
            return e.getLocalizedMessage();
        }
    }


}
