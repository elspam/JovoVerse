package com.JoyoPaten.JovoVerse.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import com.JoyoPaten.JovoVerse.model.itemLibrary;
import com.JoyoPaten.JovoVerse.model.user;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenuController {
    @Autowired
    private user user; 

    @GetMapping("/rak-buku")
    public String rakBukuPage(Model model) {
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

    @GetMapping("/tambah-item")
    public String showTambahItemForm(HttpSession session) {
        // Pastikan user sudah login dan role = 1
        user user = (user) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            return "redirect:/login"; // atau unauthorized page
        }
        return "tambah-item"; // file src/main/resources/templates/tambah-item.html
    }

    @GetMapping("/edit-item")
    public String showEditItemForm(HttpSession session, Model model) {
        user user = (user) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            return "redirect:/login";
        }
        List<itemLibrary> itemList = user.findAll();
        model.addAttribute("itemList", itemList);
        model.addAttribute("page", "daftar-buku");
        return "daftar-buku";
    }

    @GetMapping("/acc-peminjaman")
    public String showAccPeminjaman(HttpSession session) {
        user user = (user) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            return "redirect:/login";
        }
        return "acc-peminjaman";
    }


}
