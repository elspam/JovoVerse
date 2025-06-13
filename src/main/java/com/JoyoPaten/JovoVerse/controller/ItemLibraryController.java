package com.JoyoPaten.JovoVerse.controller;

import com.JoyoPaten.JovoVerse.model.Buku;
import com.JoyoPaten.JovoVerse.model.Jurnal;
import com.JoyoPaten.JovoVerse.model.itemLibrary;
import com.JoyoPaten.JovoVerse.repository.itemLibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@RequestMapping("/items")
public class ItemLibraryController {

    @Autowired
    private itemLibraryRepository repo;

    @PostMapping(value = "/buku", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addBuku(
            @RequestParam("idItem") String idItem,
            @RequestParam("judul") String judul,
            @RequestParam("tahunTerbit") int tahunTerbit,
            @RequestParam("penulis") String penulis,
            @RequestParam("halaman") int halaman,
            @RequestParam("stok") int stok,
            @RequestParam("isbn") String isbn,
            @RequestParam("cover") MultipartFile cover
    ) {
        try {
            String projectDir = System.getProperty("user.dir");
            Path coverPath = Paths.get(projectDir, "src", "main", "resources", "static", "cover");

            if (!Files.exists(coverPath)) {
                Files.createDirectories(coverPath);
            }

            String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
            Path filePath = coverPath.resolve(fileName);
            Files.copy(cover.getInputStream(), filePath);

            String coverUrl = "/cover/" + fileName;

            Buku buku = new Buku(
                    idItem, judul, tahunTerbit, penulis, halaman,
                    coverUrl,stok, isbn
            );

            return repo.save(buku);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tambah jurnal (dengan file cover)
    @PostMapping(value = "/jurnal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addJurnal(
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
        try {
            String projectDir = System.getProperty("user.dir");
            Path coverPath = Paths.get(projectDir, "src", "main", "resources", "static", "cover");

            if (!Files.exists(coverPath)) {
                Files.createDirectories(coverPath);
            }

            String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
            Path filePath = coverPath.resolve(fileName);

            Files.copy(cover.getInputStream(), filePath);
            String coverUrl = "/cover/" + fileName;

            Jurnal jurnal = new Jurnal(
                    idItem, judul, tahunTerbit, penulis, halaman,
                    coverUrl, stok, volume, noEdisi, issn
            );

            boolean saved = repo.save(jurnal);
            if (saved) {
                return ResponseEntity.ok("Berhasil menambahkan jurnal!");
            } else {
                return ResponseEntity.status(500).body("Gagal menyimpan ke database.");
            }

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Terjadi error saat upload cover: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error umum: " + e.getMessage());
        }
    }


    @PutMapping(value = "/buku/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean updateBuku(
            @PathVariable("id") String idItem,
            @RequestParam("judul") String judul,
            @RequestParam("tahunTerbit") int tahunTerbit,
            @RequestParam("penulis") String penulis,
            @RequestParam("halaman") int halaman,
            @RequestParam("stok") int stok,
            @RequestParam("isbn") String isbn,
            @RequestParam(value = "cover", required = false) MultipartFile cover // optional
    ) {
        Buku existing = (Buku) repo.findById(idItem);
        if (existing == null) return false;

        String coverUrl = existing.getCover();

        try {
            if (cover != null && !cover.isEmpty()) {
                // Hapus cover lama
                if (coverUrl != null && coverUrl.startsWith("/cover/")) {
                    File oldFile = new File("src/main/resources/static" + coverUrl);
                    if (oldFile.exists()) oldFile.delete();
                }

                // Simpan cover baru
                String projectDir = System.getProperty("user.dir");
                Path coverPath = Paths.get(projectDir, "src", "main", "resources", "static", "cover");
                if (!Files.exists(coverPath)) Files.createDirectories(coverPath);

                String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
                Path filePath = coverPath.resolve(fileName);
                Files.copy(cover.getInputStream(), filePath);

                coverUrl = "/cover/" + fileName;
            }

            Buku updated = new Buku(idItem, judul, tahunTerbit, penulis, halaman, coverUrl,stok, isbn);
            return repo.update(updated);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @PutMapping(value = "/jurnal/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean updateJurnal(
            @PathVariable("id") String idItem,
            @RequestParam("judul") String judul,
            @RequestParam("tahunTerbit") int tahunTerbit,
            @RequestParam("penulis") String penulis,
            @RequestParam("halaman") int halaman,
            @RequestParam("stok") int stok,
            @RequestParam("issn") String issn,
            @RequestParam("volume") int volume,
            @RequestParam("noEdisi") int noEdisi,
            @RequestParam(value = "cover", required = false) MultipartFile cover
    ) {
        Jurnal existing = (Jurnal) repo.findById(idItem);
        if (existing == null) return false;

        String coverUrl = existing.getCover();

        try {
            if (cover != null && !cover.isEmpty()) {
                if (coverUrl != null && coverUrl.startsWith("/cover/")) {
                    File oldFile = new File("src/main/resources/static" + coverUrl);
                    if (oldFile.exists()) oldFile.delete();
                }

                String projectDir = System.getProperty("user.dir");
                Path coverPath = Paths.get(projectDir, "src", "main", "resources", "static", "cover");
                if (!Files.exists(coverPath)) Files.createDirectories(coverPath);

                String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
                Path filePath = coverPath.resolve(fileName);
                Files.copy(cover.getInputStream(), filePath);

                coverUrl = "/cover/" + fileName;
            }

            Jurnal updated = new Jurnal(idItem, judul, tahunTerbit, penulis, halaman,
                    coverUrl,stok, volume, noEdisi, issn);

            return repo.update(updated);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Ambil semua item
    @GetMapping
    public List<itemLibrary> getAllItems() {
        return repo.findAll();
    }

    // Ambil item berdasarkan ID
    @GetMapping("/{id}")
    public itemLibrary getItemById(@PathVariable String id) {
        return repo.findById(id);
    }

    // Hapus item + file cover-nya kalau ada
    @DeleteMapping("/{id}")
    public boolean deleteItem(@PathVariable String id) {
        itemLibrary item = repo.findById(id);
        if (item == null) return false;

        // Hapus file cover (kalau ada dan path-nya dari /cover/)
        if (item instanceof Jurnal) {
            Jurnal jurnal = (Jurnal) item;
            String coverPath = jurnal.getCover(); // contoh: "/cover/123_gambar.jpg"

            if (coverPath != null && coverPath.startsWith("/cover/")) {
                File file = new File("src/main/resources/static" + coverPath);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        return repo.delete(id);
    }
}
