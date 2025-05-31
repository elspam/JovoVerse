package com.JoyoPaten.JovoVerse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
    @RequestMapping("/Login")
    public String index(){
        return "Login.html";

    }
}
