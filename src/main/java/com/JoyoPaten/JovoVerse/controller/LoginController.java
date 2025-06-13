package com.JoyoPaten.JovoVerse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.JoyoPaten.JovoVerse.model.admin;
import com.JoyoPaten.JovoVerse.model.peminjam;
import com.JoyoPaten.JovoVerse.model.user; // Sesuaikan nama repository

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    private user user; // Inject repository
    
    @GetMapping("/login")
    public String loginPage() {
        return "Login"; // Ganti dengan nama file HTML login Anda
    }


   @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Cek apakah user sudah login
        user user = (user) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", user.getUsername()); 
        if (user.getRole() == 1) {
            return "tambah-item";
        } else {
            return "home";
        }
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/register") // Tambahkan ini untuk halaman register
    public String registerPage() {
        return "register";
    }
    

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        user u = user.findByUsername(username); // Ambil data dari DB

        if (u != null && u.getPassword().equals(password)) {
            // Cek role, bikin instance sesuai role
            if (u.getRole() == 1) {
                admin adminUser = new admin(u.getUsername(), u.getPassword());
                session.setAttribute("user", adminUser);
            } else {
                peminjam peminjamUser = new peminjam(u.getUsername(), u.getPassword());
                session.setAttribute("user", peminjamUser);
            }
            return "redirect:/dashboard";
        }

        return "redirect:/Login?error=true";
    }

    @PostMapping("/register")
    public String registerPost(@RequestParam String username,@RequestParam String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/register?error=empty";
        }

        if (user.findByUsername(username) == null) {
            user.save(new user(username, password, 0));
            return "redirect:/login?success=true"; // Registrasi berhasil, redirect ke login
        } else {
            return "redirect:/register?error=userexists"; // Username sudah digunakan
        }
    }
}