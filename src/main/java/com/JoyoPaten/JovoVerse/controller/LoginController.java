package com.JoyoPaten.JovoVerse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;



import com.JoyoPaten.JovoVerse.model.user;
import com.JoyoPaten.JovoVerse.repository.userRepository;


import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private userRepository userRepo;
   
 
    @GetMapping("/login")
    public String loginPage() {
        return "Login"; // Ganti dengan nama file HTML yang sesuai
    }

    @GetMapping("/register") // Tambahkan ini untuk halaman register
    public String registerPage() {
        return "Register";
    }
    

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        if (userRepo.validate(username, password)) {
            user u = userRepo.findByUsername(username);
            session.setAttribute("user", u);
            return "redirect:/dashboard";
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Cek apakah user sudah login
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", session.getAttribute("user"));
        return "home"; // Ini mengarah ke home.html di templates/
    }

  

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @PostMapping("/register")
    public String registerPost(@RequestParam String username,@RequestParam String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/register?error=empty";
        }

        if (userRepo.findByUsername(username) == null) {
            userRepo.save(new user(username, password, 0));
            return "redirect:/login?success=true"; // Registrasi berhasil, redirect ke login
        } else {
            return "redirect:/register?error=userexists"; // Username sudah digunakan
        }
    }
}