package com.JoyoPaten.JovoVerse.model;

import com.JoyoPaten.JovoVerse.repository.JDBC;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class admin extends user {

    public admin() {
        super();
    }
    public admin(String username, String password) {
        super(username, password, 1); // 1 menandakan role admin
    }

    public boolean save(itemLibrary item) {
        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            String itemSql = "INSERT INTO item_library (id_item, judul, tahun_terbit, penulis, halaman, cover, stok) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
                    Jurnal jurnal = (Jurnal) item;
                    stmt.setString(1, jurnal.getIdItem());
                    stmt.setString(2, jurnal.getIssn());
                    stmt.setInt(3, jurnal.getVolume());
                    stmt.setInt(4, jurnal.getNoEdisi());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            // Penanganan error yang lebih informatif
            if (e.getErrorCode() == 1062) { // Kode error untuk duplicate entry
                System.err.println("GAGAL MENYIMPAN: Data duplikat. Pesan Error: " + e.getMessage());
                if (e.getMessage().contains("PRIMARY")) {
                    // Memberikan pesan spesifik jika ID Item sudah ada
                    // Pesan ini bisa diteruskan ke front-end
                } else if (e.getMessage().contains("issn") || e.getMessage().contains("isbn")) {
                    // Memberikan pesan spesifik jika ISSN atau ISBN sudah ada
                }
            } else {
                System.err.println("SQL Error saat menyimpan item: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(itemLibrary item) {
        String updateItemSql = "UPDATE item_library SET judul=?, tahun_terbit=?, penulis=?, halaman=?, stok=?, cover=? WHERE id_item=?";
        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            // Update tabel item_library
            try (PreparedStatement stmt = conn.prepareStatement(updateItemSql)) {
                stmt.setString(1, item.getJudul());
                stmt.setInt(2, item.getTahunTerbit());
                stmt.setString(3, item.getPenulis());
                stmt.setInt(4, item.getHalaman());
                stmt.setInt(5, item.getStok());
                stmt.setString(6, item.getCover());
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

            // Hapus dari tabel anak (buku/jurnal) terlebih dahulu untuk menghindari error foreign key
            String delBukuSql = "DELETE FROM buku WHERE id_item = ?";
            try (PreparedStatement delBuku = conn.prepareStatement(delBukuSql)) {
                delBuku.setString(1, id);
                delBuku.executeUpdate();
            }

            String delJurnalSql = "DELETE FROM jurnal WHERE id_item = ?";
            try (PreparedStatement delJurnal = conn.prepareStatement(delJurnalSql)) {
                delJurnal.setString(1, id);
                delJurnal.executeUpdate();
            }

            // Hapus dari tabel induk
            String delItemSql = "DELETE FROM item_library WHERE id_item = ?";
            try (PreparedStatement delItem = conn.prepareStatement(delItemSql)) {
                delItem.setString(1, id);
                int rowsAffected = delItem.executeUpdate();
                
                // Jika tidak ada baris yang terhapus di tabel utama, berarti item tidak ada
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
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

    public List<TransaksiDetail> findPendingTransaksiDetails() {
        List<TransaksiDetail> details = new ArrayList<>();

        String sql = "SELECT t.username, t.id_item, t.tanggalPinjam, t.tanggalKembali, t.deadline, t.denda, t.status " +
                    "FROM transaksi t " +
                    "WHERE t.status = 0";

        try (Connection conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Transaksi transaksi = new Transaksi(
                    rs.getString("username"),
                    rs.getString("id_item"),
                    rs.getDate("tanggalPinjam"),
                    rs.getDate("tanggalKembali"),
                    rs.getDate("deadline"),
                    rs.getInt("denda"),
                    rs.getInt("status")
                );
                if (transaksi != null) {
                    itemLibrary item = this.findById(transaksi.getIdItem());
                    if (item != null) {
                        TransaksiDetail detail = new TransaksiDetail(transaksi, item);
                        details.add(detail);
                    } else {
                        System.err.println("ERROR: Item dengan ID " + transaksi.getIdItem() + " tidak ditemukan untuk transaksi pending.");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR saat fetch transaksi pending:");
            e.printStackTrace();
        }

        return details;
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
