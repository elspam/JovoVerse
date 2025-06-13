package com.JoyoPaten.JovoVerse.model;

public class user {
    private String username;
    private String password;
    private int role;     // 1 = admin, 0 = user

    // Constructor
    public user(String username, String password, int role) {
        this.username = username;
        this.password = password;
        this.role = role; // default user
    }

    // Login method (hanya contoh, validasi biasanya di controller atau repository)
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // Getter & Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
