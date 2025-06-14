package com.JoyoPaten.JovoVerse.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.JoyoPaten.JovoVerse.model.itemLibrary;
import com.JoyoPaten.JovoVerse.model.user;
import com.JoyoPaten.JovoVerse.model.TransaksiDetail;
import com.JoyoPaten.JovoVerse.model.admin;
import com.JoyoPaten.JovoVerse.model.Buku;
import com.JoyoPaten.JovoVerse.model.Jurnal;
import com.JoyoPaten.JovoVerse.model.Transaksi;
import com.JoyoPaten.JovoVerse.model.peminjam;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MenuController {
    @Autowired
    private user user;
    
    @Autowired
    private admin admin;

    @GetMapping("/rak-buku")
    public String rakBukuPage(HttpSession session, Model model) {
        user userInSession = (user) session.getAttribute("user");
        if (userInSession == null) {
            return "redirect:/login";
        }

        peminjam peminjamService = new peminjam(userInSession.getUsername(), userInSession.getPassword());
        List<Transaksi> semuaTransaksi = peminjamService.findByUsername1(userInSession.getUsername());

        // Filter transaksi yang sedang dipinjam (status 0 = menunggu, 1 = dipinjam)
        List<TransaksiDetail> sedangDipinjam = semuaTransaksi.stream()
            .filter(t -> t.getStatus() == 0 || t.getStatus() == 1)
            .map(t -> new TransaksiDetail(t, this.user.findById(t.getIdItem())))
            .collect(Collectors.toList());

        // Filter transaksi yang sudah selesai (status 2 = kembali, 3 = terlambat)
        List<TransaksiDetail> riwayatPeminjaman = semuaTransaksi.stream()
            .filter(t -> t.getStatus() == 2 || t.getStatus() == 3)
            .map(t -> new TransaksiDetail(t, this.user.findById(t.getIdItem())))
            .collect(Collectors.toList());

        model.addAttribute("sedangDipinjam", sedangDipinjam);
        model.addAttribute("riwayatPeminjaman", riwayatPeminjaman);
        model.addAttribute("page", "rak-buku");
        return "rak-buku";
    }

    @GetMapping("/daftar-buku")
    public String daftarBukuPage(Model model) {
        List<itemLibrary> itemList = user.findAll();
        model.addAttribute("itemList", itemList);
        model.addAttribute("page", "daftar-buku");
        return "daftar-buku";
    }

    @PostMapping("/search-item")
    public String searchItem(@RequestParam("keyword") String keyword, Model model) {
        itemLibrary result = user.findByTitle(keyword);
        List<itemLibrary> itemList = new ArrayList<>();
        if (result != null) {
            itemList.add(result);
        }
        model.addAttribute("itemList", itemList);
        model.addAttribute("page", "daftar-buku");
        return "daftar-buku";
    }

    @GetMapping("/tambah-buku")
    public String showTambahBukuForm(HttpSession session, Model model) {
        user user = (user) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            return "redirect:/login"; // Hanya admin yang boleh mengakses
        }
        model.addAttribute("page", "tambah-item"); // Untuk highlight menu utama
        return "tambah-buku"; // Mengarahkan ke file tambah-buku.html
    }

    @GetMapping("/tambah-jurnal")
    public String showTambahJurnalForm(HttpSession session, Model model) {
        user user = (user) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            return "redirect:/login";
        }
        model.addAttribute("page", "tambah-item"); // Untuk highlight menu utama
        return "tambah-jurnal"; // Mengarahkan ke file tambah-jurnal.html
    }

    @GetMapping("/edit-item")
    public String showEditItemPage(HttpSession session, Model model) {
        user userInSession = (user) session.getAttribute("user");
        if (userInSession == null || userInSession.getRole() != 1) {
            return "redirect:/login";
        }
        List<itemLibrary> itemList = user.findAll();
        model.addAttribute("itemList", itemList);
        model.addAttribute("page", "edit-item"); // Untuk menu aktif di dashboard
        return "edit-item"; // Mengarahkan ke edit-item.html yang baru
    }

    // --- PENAMBAHAN BARU: Metode untuk menampilkan form edit BUKU ---
    @GetMapping("/edit-buku/{id}")
    public String showEditBukuForm(@PathVariable("id") String id, HttpSession session, Model model) {
        user userInSession = (user) session.getAttribute("user");
        if (userInSession == null || userInSession.getRole() != 1) {
            return "redirect:/login";
        }

        itemLibrary item = user.findById(id);
        if (item instanceof Buku) {
            model.addAttribute("buku", item);
            model.addAttribute("page", "edit-item"); // Agar menu tetap aktif
            return "edit-buku"; // Mengarahkan ke file edit-buku.html
        }
        // Jika ID tidak ditemukan atau bukan buku, kembali ke halaman manajemen
        return "redirect:/edit-item?error=notfound";
    }

    // --- PENAMBAHAN BARU: Metode untuk menampilkan form edit JURNAL ---
    @GetMapping("/edit-jurnal/{id}")
    public String showEditJurnalForm(@PathVariable("id") String id, HttpSession session, Model model) {
        user userInSession = (user) session.getAttribute("user");
        if (userInSession == null || userInSession.getRole() != 1) {
            return "redirect:/login";
        }

        itemLibrary item = user.findById(id);
        if (item instanceof Jurnal) {
            model.addAttribute("jurnal", item);
            model.addAttribute("page", "edit-item"); // Agar menu tetap aktif
            return "edit-jurnal"; // Mengarahkan ke file edit-jurnal.html
        }
        return "redirect:/edit-item?error=notfound";
    }

   @GetMapping("/acc-peminjaman")
    public String showAccPeminjaman(HttpSession session, Model model) {
        user userInSession = (user) session.getAttribute("user");

        if (userInSession == null || userInSession.getRole() != 1) {
            return "redirect:/login"; // hanya untuk admin
        }

        // SET data user login ke instance admin (yang sudah @Autowired)
        admin.setUsername(userInSession.getUsername());
        admin.setPassword(userInSession.getPassword());

        List<TransaksiDetail> pendingList = admin.findPendingTransaksiDetails();

        model.addAttribute("pendingList", pendingList);
        model.addAttribute("page", "acc-peminjaman");

        return "acc-peminjaman";
    }

     
    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        user userInSession = (user) session.getAttribute("user");
        if (userInSession == null) {
            return "redirect:/login"; // Jika belum login, arahkan ke halaman login
        }

        // Inisialisasi service/model yang diperlukan
        peminjam peminjamService = new peminjam(userInSession.getUsername(), userInSession.getPassword());
        
        // Ambil semua transaksi oleh pengguna
        List<Transaksi> daftarTransaksi = peminjamService.findByUsername1(userInSession.getUsername());

        // Hitung statistik
        long totalPinjam = daftarTransaksi.size();
        long sedangPinjam = daftarTransaksi.stream().filter(t -> t.getStatus() == 1).count();
        long terlambat = daftarTransaksi.stream().filter(t -> t.getStatus() == 1 && t.getDeadline() != null && t.getDeadline().toLocalDate().isBefore(LocalDate.now())).count();

        // Buat daftar detail transaksi untuk ditampilkan di riwayat
        List<TransaksiDetail> riwayatTransaksi = new ArrayList<>();
        for (Transaksi tx : daftarTransaksi) {
            itemLibrary item = this.user.findById(tx.getIdItem()); // this.user dari @Autowired
            if (item != null) {
                riwayatTransaksi.add(new TransaksiDetail(tx, item));
            }
        }
        
        // Urutkan riwayat berdasarkan tanggal pinjam terbaru
        Collections.reverse(riwayatTransaksi);

        model.addAttribute("totalPinjam", totalPinjam);
        model.addAttribute("sedangPinjam", sedangPinjam);
        model.addAttribute("terlambat", terlambat);
        model.addAttribute("riwayatTransaksi", riwayatTransaksi);
        
        model.addAttribute("page", "home"); // Untuk highlight menu aktif di dashboard
        return "home";
    }

}
