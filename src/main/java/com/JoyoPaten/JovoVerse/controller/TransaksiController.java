package com.JoyoPaten.JovoVerse.controller;

import com.JoyoPaten.JovoVerse.model.Transaksi;
import com.JoyoPaten.JovoVerse.repository.transaksiRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transaksi")
public class TransaksiController {

    @Autowired
    private transaksiRepository trxRepo;

    // Ambil semua transaksi
    @GetMapping("/all")
    public List<Transaksi> getAllTransaksi() {
        return trxRepo.findAll();
    }

    // Ambil transaksi berdasarkan username
    @GetMapping
    public List<Transaksi> getByUsername(@RequestParam String username) {
        return trxRepo.findByUsername(username);
    }

    // Simpan transaksi baru
    @PostMapping("/save")
    public boolean saveTransaksi(@RequestParam String username,
                                 @RequestParam String idItem) {
        // Transaksi baru: belum dikembalikan, maka status = 1, denda = 0
        Transaksi trx = new Transaksi(username, idItem, null, null, null, 0, 0);
        return trxRepo.save(trx);
        
    }

    // Update status transaksi menjadi "dipinjam"
    @PostMapping("/pinjam")
    public boolean pinjam(@RequestParam String username,
                          @RequestParam String idItem) {
        return trxRepo.updateStatus(username, idItem);
    }

    // Update transaksi saat pengembalian
    @PostMapping("/kembali")
    public boolean kembali(@RequestParam String username,
                           @RequestParam String idItem,
                           @RequestParam Date deadline) {
        return trxRepo.updateKembali(username, idItem, deadline);
    }

    @PostMapping("/perpanjang")
    public ResponseEntity<String> perpanjangDeadline(
            @RequestParam String username,
            @RequestParam String idItem,
            @RequestParam Date deadline) {

        boolean success = trxRepo.Perpanjang(username, idItem, deadline);

        if (success) {
            return ResponseEntity.ok("Deadline berhasil diperpanjang 7 hari.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Gagal memperpanjang deadline.");
        }
    }
}
