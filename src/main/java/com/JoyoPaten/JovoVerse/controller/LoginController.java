package com.JoyoPaten.JovoVerse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.JoyoPaten.JovoVerse.model.user;
import com.JoyoPaten.JovoVerse.repository.userRepository; // Sesuaikan nama repository

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private userRepository userRepo; // Inject repository
    

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/Login.html";
    }

    @GetMapping("/register") // Tambahkan ini untuk halaman register
    public String registerPage() {
        return "redirect:/Register.html";
    }
    

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        if (userRepo.validate(username, password)) {
            session.setAttribute("user", username);
            return "redirect:/dashboard.html";
        }
        return "redirect:/Login?error=true";
    }

    @PostMapping("/register")
    public String registerPost(@RequestParam String username,@RequestParam String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/register?error=empty";
        }

        if (userRepo.findByUsername(username) == null) {
            userRepo.save(new user(username, password, 0, 0.0));
            return "redirect:/login?success=true"; // Registrasi berhasil, redirect ke login
        } else {
            return "redirect:/register?error=userexists"; // Username sudah digunakan
        }
    }
}