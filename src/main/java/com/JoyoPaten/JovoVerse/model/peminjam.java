package com.JoyoPaten.JovoVerse.model;

import com.JoyoPaten.JovoVerse.repository.JDBC;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class peminjam extends user {

    public peminjam(String username, String password) {
        super(username, password, 0); 
    }

    public boolean save(Transaksi trx) {
        String sql = "INSERT INTO transaksi (username, id_item, tanggalPinjam, tanggalKembali, deadline,denda,status) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trx.getUsername());
            stmt.setString(2, trx.getIdItem());
            stmt.setDate(3, trx.getTanggalPinjam());
            stmt.setDate(4, trx.getTanggalKembali());
            stmt.setDate(5, trx.getDeadline());
            stmt.setInt(6, trx.getDenda());
            stmt.setInt(7, trx.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Gagal menyimpan transaksi!");
            System.out.println("Pesan error: " + e.getMessage());
            e.printStackTrace(); // Tambahkan ini agar tahu baris error-nya
            return false;
        }
    }

    public List<Transaksi> findByUsername1(String username) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi WHERE username = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Transaksi(
                    rs.getString("username"),
                    rs.getString("id_item"),
                    rs.getDate("tanggalPinjam"),
                    rs.getDate("tanggalKembali"),
                    rs.getDate("deadline"),
                    rs.getInt("denda"),
                    rs.getInt("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean Perpanjang(String username, String idItem, Date deadline) {
        String sql = "UPDATE transaksi SET deadline = ? WHERE username = ? AND id_item = ?";
        try (Connection conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ambil tanggal hari ini
            LocalDate today = LocalDate.now();

            // Ubah java.sql.Date (deadline) ke LocalDate
            LocalDate deadlineLocal = deadline.toLocalDate();

            // Cek apakah hari ini adalah H-1 dari deadline
            if (!today.equals(deadlineLocal.minusDays(1))) {
                System.out.println("Perpanjangan hanya boleh dilakukan H-1 sebelum deadline!");
                return false;
            }

            // Tambahkan 7 hari ke deadline
            LocalDate newDeadline = deadlineLocal.plusDays(7);

            // Konversi kembali ke java.sql.Date
            Date sqlDeadline = Date.valueOf(newDeadline);

            // Lanjutkan update
            stmt.setDate(1, sqlDeadline);
            stmt.setString(2, username);
            stmt.setString(3, idItem);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    

}
