package com.kc.Utilities; // 12 Mar, 07:18 PM

import java.sql.*;

import static com.kc.Utilities.C.*;

/**
 * Class that helps in direct database access instead using php
 */

public class DatabaseHelper {


    public Connection connection;
    public Statement statement;
    public ResultSet resultSet;
    public String result;

    public void launchQuery(final String query) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_CON, JDBC_USER, JDBC_PASS);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);// return resultset
        } catch (SQLException | ClassNotFoundException e) {
            C.SQL_RESULT = e.getLocalizedMessage();
        }
    }

    /**
     * For Update and insert queries
     *
     * @param query
     * @param update - update or insert?
     * @return
     */
    public String launchUpdate(final String query, boolean update) {

        Connection connection = null;
        Statement statement = null;

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
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
