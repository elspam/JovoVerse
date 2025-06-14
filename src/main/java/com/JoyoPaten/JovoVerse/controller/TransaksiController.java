package com.JoyoPaten.JovoVerse.controller;

import com.JoyoPaten.JovoVerse.model.Transaksi;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.JoyoPaten.JovoVerse.model.admin;
import com.JoyoPaten.JovoVerse.model.peminjam;
import com.JoyoPaten.JovoVerse.model.user;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaksi")
public class TransaksiController {

    // --- PERBAIKAN: Inisialisasi service di constructor ---
    private final admin adminService;
    private final peminjam peminjamService;

    public TransaksiController() {
        // Inisialisasi dengan user default, karena metode yang akan digunakan
        // tidak bergantung pada state internal username/password objek ini.
        this.adminService = new admin("default", "default");
        this.peminjamService = new peminjam("default", "default");
    }

    // Ambil semua transaksi (hanya untuk admin)
    @GetMapping("/all")
    public ResponseEntity<?> getAllTransaksi(HttpSession session) {
        if (!(session.getAttribute("user") instanceof admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak");
        }
        return ResponseEntity.ok(adminService.findAll1());
    }

    // Ambil transaksi berdasarkan username
    @GetMapping
    public List<Transaksi> getByUsername(@RequestParam String username) {
        return peminjamService.findByUsername1(username);
    }

    // Simpan transaksi baru (pengajuan peminjaman oleh user)
    @PostMapping("/save")
    public boolean saveTransaksi(@RequestParam String username, @RequestParam String idItem, HttpSession session) {
        if (!(session.getAttribute("user") instanceof peminjam)) {
            return false; // Pastikan hanya peminjam yang bisa mengajukan
        }
        peminjam user = (peminjam) session.getAttribute("user");
        // Status 0: Menunggu ACC
        Date defaultDate = Date.valueOf("1970-01-01");
        Transaksi trx = new Transaksi(username, idItem, defaultDate, defaultDate, defaultDate, 0, 0);
        return user.save(trx);
    }

    // ACC Peminjaman (dilakukan oleh admin)
    @PostMapping("/acc")
    public boolean pinjam(@RequestParam String username, @RequestParam String idItem, HttpSession session) {
        if (!(session.getAttribute("user") instanceof admin)) {
            return false; // Hanya admin yang bisa ACC
        }
        admin user = (admin) session.getAttribute("user");
        return user.updateStatus(username, idItem);
    }

    // Perpanjang Deadline (dilakukan oleh peminjam)
    @PostMapping("/perpanjang")
    public ResponseEntity<String> perpanjangDeadline(
            @RequestParam String username,
            @RequestParam String idItem,
            HttpSession session) {

        if (!(session.getAttribute("user") instanceof peminjam)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak.");
        }
        peminjam user = (peminjam) session.getAttribute("user");

        // Ambil transaksi dari database untuk mendapatkan deadline saat ini
        List<Transaksi> userTransactions = peminjamService.findByUsername1(username);
        Optional<Transaksi> trxToExtendOpt = userTransactions.stream()
                .filter(t -> t.getIdItem().equals(idItem) && t.getStatus() == 1)
                .findFirst();

        if (trxToExtendOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaksi tidak ditemukan atau sudah dikembalikan.");
        }

        boolean success = user.Perpanjang(username, idItem, trxToExtendOpt.get().getDeadline());

        if (success) {
            return ResponseEntity.ok("Deadline berhasil diperpanjang 7 hari.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Gagal memperpanjang deadline. Perpanjangan hanya bisa H-1 deadline.");
        }
    }

    @PostMapping("/kembali")
    public boolean kembali(@RequestParam String username, @RequestParam String idItem, @RequestParam Date deadline, HttpSession session) {
        if (!(session.getAttribute("user") instanceof admin)) {
            return false; // Hanya admin yang bisa ACC
        }
        admin user = (admin) session.getAttribute("user");
        return user.updateKembali(username, idItem, deadline);
    }
}