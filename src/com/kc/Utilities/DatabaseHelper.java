package com.kc.Utilities; // 12 Mar, 07:18 PM

import java.sql.*;

import static com.kc.Utilities.C.*;

/**
 * Class that helps in direct database access instead using php
 */

public class DatabaseHelper {

    /**
     * <p>Ask a query and get the result in the form of cursor.</p>
     *
     * @param query
     * @return
     * @throws SQLException
     */
    static Connection connection = null;
    static Statement statement = null;

    public static ResultSet launchQuery(final String query) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_CON, JDBC_USER, JDBC_PASS);
            statement = connection.createStatement();
            return statement.executeQuery(query);// return resultset
        } catch (SQLException e) {
            C.SQL_RESULT = e.getLocalizedMessage();
            return null;
        } catch (ClassNotFoundException e) {
            C.SQL_RESULT = e.getLocalizedMessage();
            return null;
        }
    }

    /**
     * For Update and insert queries
     *
     * @param query
     * @param update - update or insert?
     * @return
     */
    public static String launchUpdate(final String query, boolean update) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_CON, JDBC_USER, JDBC_PASS);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            if (update) {
                if (query.charAt(0) == 'd' || query.charAt(0) == 'D') {
                    return "oxo" + i;
                } else {
                    return "oxo" + i;
                }
            } else {
                return "oxo" + i;
            }
        } catch (SQLException e) {
            return e.getLocalizedMessage();
        } catch (ClassNotFoundException e) {
            return e.getLocalizedMessage();
        }
    }

    public static void close() {
        try {

            if (statement != null) statement.close();
            if (connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
