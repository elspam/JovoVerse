package com.JoyoPaten.JovoVerse.controller;

import com.JoyoPaten.JovoVerse.model.Transaksi;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.JoyoPaten.JovoVerse.model.admin;
import com.JoyoPaten.JovoVerse.model.peminjam;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transaksi")
public class TransaksiController {
    private admin admin;
    private peminjam peminjam;

    // Ambil semua transaksi
    @GetMapping("/all")
    public List<Transaksi> getAllTransaksi() {
        return admin.findAll1();
    }

    // Ambil transaksi berdasarkan username
    @GetMapping
    public List<Transaksi> getByUsername(@RequestParam String username) {
        return peminjam.findByUsername1(username);
    }

    // Simpan transaksi baru
    @PostMapping("/save")
    public boolean saveTransaksi(@RequestParam String username,
                                 @RequestParam String idItem,HttpSession session) {
        // Transaksi baru: belum dikembalikan, maka status = 1, denda = 0
        peminjam user = (peminjam) session.getAttribute("user");
        Transaksi trx = new Transaksi(username, idItem, null, null, null, 0, 0);
        return user.save(trx);
    }

    // Update status transaksi menjadi "dipinjam"
    @PostMapping("/acc")
    public boolean pinjam(@RequestParam String username,
                          @RequestParam String idItem,HttpSession session) {
        admin user = (admin) session.getAttribute("user");                    
        return user.updateStatus(username, idItem);
    }

    // Update transaksi saat pengembalian
    @PostMapping("/kembali")
    public boolean kembali(@RequestParam String username,
                           @RequestParam String idItem,
                           @RequestParam Date deadline,HttpSession session) {
        admin user = (admin) session.getAttribute("user");
        return user.updateKembali(username, idItem, deadline);
    }

    @PostMapping("/perpanjang")
    public ResponseEntity<String> perpanjangDeadline(
            @RequestParam String username,
            @RequestParam String idItem,
            @RequestParam Date deadline,HttpSession session) {
        peminjam user = (peminjam) session.getAttribute("user");
        boolean success = user.Perpanjang(username, idItem, deadline);

        if (success) {
            return ResponseEntity.ok("Deadline berhasil diperpanjang 7 hari.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Gagal memperpanjang deadline.");
        }
    }
}
