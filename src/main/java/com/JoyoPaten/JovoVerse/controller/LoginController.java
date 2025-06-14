package com.JoyoPaten.JovoVerse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.JoyoPaten.JovoVerse.model.admin;
import com.JoyoPaten.JovoVerse.model.peminjam;
import com.JoyoPaten.JovoVerse.model.user;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String loginPage() {
        return "Login"; // Mengarahkan ke Login.html
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        user userInSession = (user) session.getAttribute("user");
        if (userInSession == null) {
            return "redirect:/login";
        }
        
        // Cukup arahkan ke "home", logika tampilan menu diatur di dashboard.html
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "Register"; // Mengarahkan ke Register.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        user u = user.findByUsername(username);

        if (u != null && u.getPassword().equals(password)) {
            if (u.getRole() == 1) {
                admin adminUser = new admin(u.getUsername(), u.getPassword());
                session.setAttribute("user", adminUser);
            } else {
                peminjam peminjamUser = new peminjam(u.getUsername(), u.getPassword());
                session.setAttribute("user", peminjamUser);
            }
            return "redirect:/home"; // Langsung ke /home setelah login berhasil
        }

        return "redirect:/login?error=true"; // Kembali ke login dengan notifikasi error
    }

    @PostMapping("/register")
    public String registerPost(@RequestParam String username, @RequestParam String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/register?error=empty";
        }

        if (user.findByUsername(username) == null) {
            user.save(new user(username, password, 0)); // Role 0 untuk peminjam
            return "redirect:/login?success=true";
        } else {
            return "redirect:/register?error=userexists";
        }
    }
}