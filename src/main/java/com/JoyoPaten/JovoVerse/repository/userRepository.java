package com.JoyoPaten.JovoVerse.repository;

import com.JoyoPaten.JovoVerse.model.user;
import org.springframework.stereotype.Repository;
import java.sql.*;

@Repository
public class userRepository {

    public boolean save(user user) {
    String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
    try (Connection conn = JDBC.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        System.out.println("[DEBUG] Koneksi berhasil: " + (conn != null)); // Debug
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.setInt(3, user.getRole());

        int affected = stmt.executeUpdate();
        System.out.println("[DEBUG] Rows affected: " + affected);
        return affected > 0;

    } catch (SQLException e) {
        System.out.println("[ERROR] Gagal insert user:");
        e.printStackTrace();
        return false;
    }
}

    public user findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new user(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("role")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validate(String username, String password) {
        user user = findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
}