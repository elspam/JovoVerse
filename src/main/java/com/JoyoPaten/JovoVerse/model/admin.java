package com.JoyoPaten.JovoVerse.model;

import com.JoyoPaten.JovoVerse.repository.JDBC;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class admin extends user {

    public admin(String username, String password) {
        super(username, password, 1); // 1 menandakan role admin
    }

    public boolean save(itemLibrary item) {
        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            // Insert into item_library
            String itemSql = "INSERT INTO item_library (id_item, judul, tahun_terbit, penulis, halaman, cover,stok) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(itemSql)) {
                stmt.setString(1, item.getIdItem());
                stmt.setString(2, item.getJudul());
                stmt.setInt(3, item.getTahunTerbit());
                stmt.setString(4, item.getPenulis());
                stmt.setInt(5, item.getHalaman());
                stmt.setString(6, item.getCover());
                stmt.setInt(7, item.getStok());
                stmt.executeUpdate();
            }

            // Insert ke tabel spesifik: buku / jurnal
            if (item instanceof Buku) {
                String bukuSql = "INSERT INTO buku (id_item, isbn) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(bukuSql)) {
                    stmt.setString(1, item.getIdItem());
                    stmt.setString(2, ((Buku) item).getIsbn());
                    stmt.executeUpdate();
                }
            } else if (item instanceof Jurnal) {
                String jurnalSql = "INSERT INTO jurnal (id_item, issn, volume, no_edisi) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(jurnalSql)) {
                    stmt.setString(1, item.getIdItem());
                    stmt.setString(2, ((Jurnal) item).getIssn());
                    stmt.setInt(3, ((Jurnal) item).getVolume());
                    stmt.setInt(4, ((Jurnal) item).getNoEdisi());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(itemLibrary item) {
        String updateItemSql = "UPDATE item_library SET judul=?, tahun_terbit=?, penulis=?, halaman=?, cover=?, stok=? WHERE id_item=?";
        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            // Update tabel item_library
            try (PreparedStatement stmt = conn.prepareStatement(updateItemSql)) {
                stmt.setString(1, item.getJudul());
                stmt.setInt(2, item.getTahunTerbit());
                stmt.setString(3, item.getPenulis());
                stmt.setInt(4, item.getHalaman());
                stmt.setString(5, item.getCover());
                stmt.setInt(6, item.getStok());
                stmt.setString(7, item.getIdItem());
                stmt.executeUpdate();
            }

            // Update tabel spesifik
            if (item instanceof Buku buku) {
                String bukuSql = "UPDATE buku SET isbn=? WHERE id_item=?";
                try (PreparedStatement stmt = conn.prepareStatement(bukuSql)) {
                    stmt.setString(1, buku.getIsbn());
                    stmt.setString(2, buku.getIdItem());
                    stmt.executeUpdate();
                }
            } else if (item instanceof Jurnal jurnal) {
                String jurnalSql = "UPDATE jurnal SET issn=?, volume=?, no_edisi=? WHERE id_item=?";
                try (PreparedStatement stmt = conn.prepareStatement(jurnalSql)) {
                    stmt.setString(1, jurnal.getIssn());
                    stmt.setInt(2, jurnal.getVolume());
                    stmt.setInt(3, jurnal.getNoEdisi());
                    stmt.setString(4, jurnal.getIdItem());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String id) {
        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            // Ambil info cover terlebih dahulu
            itemLibrary item = findById(id);
            String coverFilename = item != null ? item.getCover() : null;

            // Hapus dari tabel buku/jurnal
            try (PreparedStatement delBuku = conn.prepareStatement("DELETE FROM buku WHERE id_item = ?")) {
                delBuku.setString(1, id);
                delBuku.executeUpdate();
            }
            try (PreparedStatement delJurnal = conn.prepareStatement("DELETE FROM jurnal WHERE id_item = ?")) {
                delJurnal.setString(1, id);
                delJurnal.executeUpdate();
            }
            try (PreparedStatement delItem = conn.prepareStatement("DELETE FROM item_library WHERE id_item = ?")) {
                delItem.setString(1, id);
                delItem.executeUpdate();
            }

            conn.commit();

            // Hapus file gambar
            if (coverFilename != null) {
                File coverFile = new File("uploads/covers/" + coverFilename);
                if (coverFile.exists()) {
                    coverFile.delete();
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaksi> findAll1() {
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
    

}
