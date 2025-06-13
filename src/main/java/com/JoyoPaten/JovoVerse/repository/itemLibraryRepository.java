package com.JoyoPaten.JovoVerse.repository;

import com.JoyoPaten.JovoVerse.model.Buku;
import com.JoyoPaten.JovoVerse.model.Jurnal;
import com.JoyoPaten.JovoVerse.model.itemLibrary;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;


@Repository
public class itemLibraryRepository {

    public boolean save(itemLibrary item) {
        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            // Insert into item_library
            String itemSql = "INSERT INTO item_library (id_item, judul, tahun_terbit, penulis, halaman, cover, stok) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(itemSql)) {
                stmt.setString(1, item.getIdItem());
                stmt.setString(2, item.getJudul());
                stmt.setInt(3, item.getTahunTerbit());
                stmt.setString(4, item.getPenulis());
                stmt.setInt(5, item.getHalaman());
                stmt.setString(6, item.getCover());
                stmt.setInt(7, item.getStok());
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
                    data.getStok(),
                    rs.getInt("volume"),
                    rs.getInt("no_edisi"),
                    rs.getString("issn")
                );
            }
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
                item.setStok(rs.getInt("stok"));
                item.setCover(rs.getString("cover")); // Penting
                item.setStok(rs.getInt("stok"));

                return item;
            }
        }
        return null;
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

}
