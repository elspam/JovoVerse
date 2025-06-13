package com.JoyoPaten.JovoVerse.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.JoyoPaten.JovoVerse.repository.JDBC;
import org.springframework.stereotype.Service;

@Service
public class user {
    private String username;
    private String password;
    private int role;     // 1 = admin, 0 = user

    
    public user() {
        // Default constructor for Spring to create a bean instance
    }

    // Constructor
    public user(String username, String password, int role) {
        this.username = username;
        this.password = password;
        this.role = role; // default user
    }

    // Login method (hanya contoh, validasi biasanya di controller atau repository)
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // Getter & Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public static boolean save(user user) {
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

     public static user findByUsername(String username) {
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


    public itemLibrary findById(String id) {
        String sql = "SELECT * FROM item_library WHERE id_item = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Cek apakah ada di buku
                Buku buku = findBukuById(conn, id);
                if (buku != null) return buku;

                // Cek jurnal
                Jurnal jurnal = findJurnalById(conn, id);
                if (jurnal != null) return jurnal;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<itemLibrary> findAll() {
        List<itemLibrary> items = new ArrayList<>();
        String sql = "SELECT * FROM item_library";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_item");

                // Deteksi apakah item ini buku atau jurnal
                Buku buku = findBukuById(conn, id);
                if (buku != null) {
                    items.add(buku);
                    continue;
                }

                Jurnal jurnal = findJurnalById(conn, id);
                if (jurnal != null) {
                    items.add(jurnal);
                    continue;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private Buku findBukuById(Connection conn, String id) throws SQLException {
        String sql = "SELECT * FROM buku WHERE id_item = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Ambil dari item_library juga
                itemLibrary data = getItemData(conn, id);
                return new Buku(
                    data.getIdItem(),
                    data.getJudul(),
                    data.getTahunTerbit(),
                    data.getPenulis(),
                    data.getHalaman(),
                    data.getCover(),
                    data.getStok(),
                    rs.getString("isbn")
                );
            }
        }
        return null;
    }

    private Jurnal findJurnalById(Connection conn, String id) throws SQLException {
        String sql = "SELECT * FROM jurnal WHERE id_item = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                itemLibrary data = getItemData(conn, id);
                return new Jurnal(
                    data.getIdItem(),
                    data.getJudul(),
                    data.getTahunTerbit(),
                    data.getPenulis(),
                    data.getHalaman(),
                    data.getCover(),
                    data.getStok(),
                    rs.getInt("volume"),
                    rs.getInt("no_edisi"),
                    rs.getString("issn")
                );
            }
        }
        return null;
    }

    public itemLibrary findByTitle(String judul) {
        String sql = "SELECT * FROM item_library WHERE judul = ?";
        try (Connection conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, judul);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("id_item"); // ✅ Ambil id_item, bukan judul

                Buku buku = findBukuById(conn, id);
                if (buku != null) return buku;

                Jurnal jurnal = findJurnalById(conn, id);
                if (jurnal != null) return jurnal;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private itemLibrary getItemData(Connection conn, String id) throws SQLException {
        String sql = "SELECT * FROM item_library WHERE id_item = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                itemLibrary item = new itemLibrary() {
                    @Override
                    public String getInfo() {
                        return "Base item";
                    }
                };
                item.setIdItem(rs.getString("id_item"));
                item.setJudul(rs.getString("judul"));
                item.setTahunTerbit(rs.getInt("tahun_terbit"));
                item.setPenulis(rs.getString("penulis"));
                item.setHalaman(rs.getInt("halaman"));
                item.setCover(rs.getString("cover")); // Penting
                item.setStok(rs.getInt("stok"));

                return item;
            }
        }
        return null;
    }

}
