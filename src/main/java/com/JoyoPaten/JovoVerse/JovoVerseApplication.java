package com.JoyoPaten.JovoVerse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.JoyoPaten.JovoVerse.repository.JDBC;
import java.sql.*;

@SpringBootApplication
public class JovoVerseApplication {

	public static void main(String[] args) {
		SpringApplication.run(JovoVerseApplication.class, args);

		// Test koneksi JDBC manual
        try (Connection conn = JDBC.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connected to DB!");

                String sql = "SELECT * FROM users";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                System.out.println("=== DAFTAR USERS ===");
                while (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    int role = rs.getInt("role");
                    double denda = rs.getDouble("denda");

                    String roleStr = (role == 1) ? "Admin" : "User";

                    System.out.println("Username: " + username);
                    System.out.println("Password: " + password);
                    System.out.println("Role    : " + roleStr);
                    System.out.println("Denda   : " + denda);
                    System.out.println("--------------------------");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

}
