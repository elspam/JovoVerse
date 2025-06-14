package com.JoyoPaten.JovoVerse.controller;

import com.JoyoPaten.JovoVerse.model.Buku;
import com.JoyoPaten.JovoVerse.model.admin;
import com.JoyoPaten.JovoVerse.model.Jurnal;
import com.JoyoPaten.JovoVerse.model.itemLibrary;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.ui.Model;

@RestController
@RequestMapping("/items")
public class ItemLibraryController {
    
    @PostMapping(value = "/buku", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addBuku( // UBAH return type dari boolean ke ResponseEntity<String>
            HttpSession session,
            @RequestParam("idItem") String idItem,
            @RequestParam("judul") String judul,
            @RequestParam("tahunTerbit") int tahunTerbit,
            @RequestParam("penulis") String penulis,
            @RequestParam("halaman") int halaman,
            @RequestParam("stok") int stok,
            @RequestParam("isbn") String isbn,
            @RequestParam("cover") MultipartFile cover
    ) {
        Object userObj = session.getAttribute("user");
        if (!(userObj instanceof admin)) {
            // Bukan admin, kembalikan pesan error
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak: Hanya admin yang dapat menambah item.");
        }
        admin adminUser = (admin) userObj;

        try {
            String projectDir = System.getProperty("user.dir");
            Path coverPath = Paths.get(projectDir, "src", "main", "resources", "static", "cover");

            if (!Files.exists(coverPath)) {
                Files.createDirectories(coverPath);
            }

            String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
            Path filePath = coverPath.resolve(fileName);
            try {
                Files.copy(cover.getInputStream(), filePath);
            } catch (IOException e) {
                Files.deleteIfExists(filePath);
                throw e;
            }

            String coverUrl = "/cover/" + fileName;

            Buku buku = new Buku(
                    idItem, judul, tahunTerbit, penulis, halaman, coverUrl,
                    stok, isbn
            );
            
            boolean saved = adminUser.save(buku); // Metode save dari admin.java
            
            if (saved) {
                return ResponseEntity.ok("Buku berhasil ditambahkan!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menyimpan buku. Pastikan ID Item unik.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi error saat mengunggah cover: " + e.getMessage());
        }
    }

    // Tambah jurnal (dengan file cover)
    @PostMapping(value = "/jurnal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addJurnal(
            HttpSession session,
            @RequestParam("idItem") String idItem,
            @RequestParam("judul") String judul,
            @RequestParam("tahunTerbit") int tahunTerbit,
            @RequestParam("penulis") String penulis,
            @RequestParam("halaman") int halaman,
            @RequestParam("stok") int stok,
            @RequestParam("issn") String issn,
            @RequestParam("volume") int volume,
            @RequestParam("noEdisi") int noEdisi,
            @RequestParam("cover") MultipartFile cover
    ) {
        Object userObj = session.getAttribute("user");
        if (!(userObj instanceof admin)) {
            return ResponseEntity.status(403).body("Akses ditolak: Hanya admin yang boleh menambahkan jurnal.");
        }
        admin adminUser = (admin) userObj;
        try {
            // ... (logika upload file Anda tetap sama)
            String projectDir = System.getProperty("user.dir");
            Path coverPath = Paths.get(projectDir, "src", "main", "resources", "static", "cover");
            if (!Files.exists(coverPath)) {
                Files.createDirectories(coverPath);
            }
            String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
            Path filePath = coverPath.resolve(fileName);
            Files.copy(cover.getInputStream(), filePath);
            String coverUrl = "/cover/" + fileName;

            // --- PERBAIKAN UTAMA ADA DI SINI ---
            // Pastikan urutan parameter ini sama persis dengan konstruktor Jurnal.java
            // Urutan yang benar: (..., stok, volume, noEdisi, issn)
            Jurnal jurnal = new Jurnal(
                    idItem, judul, tahunTerbit, penulis, halaman, coverUrl, 
                    stok, volume, noEdisi, issn
            );
            // ------------------------------------

            boolean saved = adminUser.save(jurnal);
            if (saved) {
                return ResponseEntity.ok("Berhasil menambahkan jurnal!");
            } else {
                return ResponseEntity.status(500).body("Gagal menyimpan ke database. Pastikan ID Item dan ISSN unik.");
            }

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Terjadi error saat upload cover: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error umum: " + e.getMessage());
        }
    }  

    @PutMapping(value = "/buku/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateBuku(
            @PathVariable("id") String idItem,
            @RequestParam String judul, @RequestParam int tahunTerbit, @RequestParam String penulis,
            @RequestParam int halaman, @RequestParam int stok, @RequestParam String isbn,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            HttpSession session) {

        Object userObj = session.getAttribute("user");
        if (!(userObj instanceof admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak.");
        }
        admin adminUser = (admin) userObj;

        itemLibrary existingItem = adminUser.findById(idItem);
        if (existingItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item tidak ditemukan.");
        }

        String coverUrl = existingItem.getCover();
        if (cover != null && !cover.isEmpty()) {
            try {
                if (coverUrl != null && !coverUrl.isBlank() && coverUrl.startsWith("/cover/")) {
                    Files.deleteIfExists(Paths.get("src/main/resources/static" + coverUrl));
                }
                String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
                Files.copy(cover.getInputStream(), Paths.get("src/main/resources/static/cover/" + fileName));
                coverUrl = "/cover/" + fileName;
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal upload cover baru.");
            }
        }

        Buku updatedBuku = new Buku(idItem, judul, tahunTerbit, penulis, halaman, coverUrl, stok, isbn);
        boolean success = adminUser.update(updatedBuku);

        return success ? ResponseEntity.ok("Buku berhasil diperbarui!") :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal memperbarui buku.");
    }

    // PERBAIKI METODE UPDATE JURNAL YANG ADA
    @PutMapping(value = "/jurnal/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateJurnal(
            @PathVariable("id") String idItem,
            @RequestParam String judul, @RequestParam int tahunTerbit, @RequestParam String penulis,
            @RequestParam int halaman, @RequestParam int stok, @RequestParam String issn,
            @RequestParam int volume, @RequestParam int noEdisi,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            HttpSession session) {

        Object userObj = session.getAttribute("user");
        if (!(userObj instanceof admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak.");
        }
        admin adminUser = (admin) userObj;

        itemLibrary existingItem = adminUser.findById(idItem);
        if (existingItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item tidak ditemukan.");
        }

        String coverUrl = existingItem.getCover();
        if (cover != null && !cover.isEmpty()) {
            try {
                if (coverUrl != null && !coverUrl.isBlank() && coverUrl.startsWith("/cover/")) {
                    Files.deleteIfExists(Paths.get("src/main/resources/static" + coverUrl));
                }
                String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
                Files.copy(cover.getInputStream(), Paths.get("src/main/resources/static/cover/" + fileName));
                coverUrl = "/cover/" + fileName;
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal upload cover baru.");
            }
        }

        Jurnal updatedJurnal = new Jurnal(idItem, judul, tahunTerbit, penulis, halaman, coverUrl, stok, volume, noEdisi, issn);
        boolean success = adminUser.update(updatedJurnal);

        return success ? ResponseEntity.ok("Jurnal berhasil diperbarui!") :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal memperbarui jurnal.");
    }
    
    // Ambil semua item
    @GetMapping
    public ResponseEntity<?> getAllItems(HttpSession session) {
        Object userObj = session.getAttribute("user");

        if (!(userObj instanceof admin)) {
            return ResponseEntity.status(403).body("Akses ditolak: Hanya admin yang boleh melihat semua item.");
        }

        admin adminUser = (admin) userObj;
        List<itemLibrary> items = adminUser.findAll();
        return ResponseEntity.ok(items);
    }

    // Ambil item berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable String id, HttpSession session) {
        Object userObj = session.getAttribute("user");

        if (!(userObj instanceof admin)) {
            return ResponseEntity.status(403).body("Akses ditolak: hanya admin yang bisa melihat item.");
        }

        admin adminUser = (admin) userObj;
        itemLibrary item = adminUser.findById(id);

        if (item == null) {
            return ResponseEntity.status(404).body("Item dengan ID " + id + " tidak ditemukan.");
        }

        return ResponseEntity.ok(item);
    }

    // Hapus item + file cover-nya kalau ada
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteItem(@PathVariable String id, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (!(userObj instanceof admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }
        admin adminUser = (admin) userObj;
        
        itemLibrary item = adminUser.findById(id);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

        boolean deletedFromDb = adminUser.delete(id);

        if (deletedFromDb) {
            String coverPath = item.getCover();
            if (coverPath != null && coverPath.startsWith("/cover/")) {
                try {
                    Files.deleteIfExists(Paths.get("src/main/resources/static" + coverPath));
                } catch (IOException e) {
                    System.err.println("Gagal hapus file cover: " + e.getMessage());
                }
            }
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
