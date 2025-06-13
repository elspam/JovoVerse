package com.JoyoPaten.JovoVerse.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;

import com.JoyoPaten.JovoVerse.model.Transaksi;

@Repository
public class transaksiRepository {
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
            return false;
        }
    }

    public List<Transaksi> findByUsername(String username) {
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

    public List<Transaksi> findAll() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi";
        try (Connection conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

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

    public boolean updateKembali(String username, String idItem, Date deadline) {
        String sql = "UPDATE transaksi SET tanggalKembali = ?, status = ?, denda = ? WHERE username = ? AND id_item = ?";
        try (Connection conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ambil tanggal hari ini sebagai tanggal kembali
            Date kembali = new Date(System.currentTimeMillis());

            // Hitung selisih hari
            long millisSelisih = kembali.getTime() - deadline.getTime();
            long hariSelisih = millisSelisih / (1000 * 60 * 60 * 24);

            // Hitung denda (jika telat)
            int denda = (hariSelisih > 0) ? (int) hariSelisih * 1000 : 0;

            // Status 2 = tepat waktu, 3 = terlambat
            int status = (hariSelisih > 0) ? 3 : 2;

            // Isi parameter query
            stmt.setDate(1, kembali);
            stmt.setInt(2, status);
            stmt.setInt(3, denda);
            stmt.setString(4, username);
            stmt.setString(5, idItem);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateStatus(String username, String idItem) {
        String sql = "UPDATE transaksi SET tanggalPinjam = ?, deadline = ?, status = ? WHERE username = ? AND id_item = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Tanggal pinjam adalah hari ini
                LocalDate pinjam = LocalDate.now();

                // Deadline = pinjam + 7 hari
                LocalDate deadline = pinjam.plusDays(7);

                // Konversi ke java.sql.Date
                Date sqlPinjam = Date.valueOf(pinjam);
                Date sqlDeadline = Date.valueOf(deadline);

                stmt.setDate(1, sqlPinjam);
                stmt.setDate(2, sqlDeadline);
                stmt.setInt(3, 1);
                stmt.setString(4, username);
                stmt.setString(5, idItem);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
