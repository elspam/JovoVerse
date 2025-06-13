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


import com.JoyoPaten.JovoVerse.repository.itemLibraryRepository;


@Controller
public class MenuController {
    @Autowired
    private itemLibraryRepository repo; 

    @GetMapping("/rak-buku")
    public String rakBukuPage(Model model) {
        model.addAttribute("page", "rak-buku");
        return "rak-buku";
    }

    @GetMapping("/daftar-buku")
    public String daftarBukuPage(Model model) {
        List<itemLibrary> itemList = repo.findAll();
        model.addAttribute("itemList", itemList);
        model.addAttribute("page", "daftar-buku");
        return "daftar-buku";
    }

    @PostMapping("/search-item")
    public String searchItem(@RequestParam("keyword") String keyword, Model model) {
        itemLibrary result = repo.findByTitle(keyword);
        List<itemLibrary> itemList = new ArrayList<>();
        if (result != null) {
            itemList.add(result);
        }
        model.addAttribute("itemList", itemList);
        model.addAttribute("page", "daftar-buku");
        return "daftar-buku";
    }

}
