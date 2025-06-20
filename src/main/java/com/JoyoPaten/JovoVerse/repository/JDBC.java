package com.JoyoPaten.JovoVerse.repository;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class JDBC {
    private static final String URL = "jdbc:mysql://localhost:3306/joyo_verse";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
