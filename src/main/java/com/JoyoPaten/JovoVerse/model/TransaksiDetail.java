package com.JoyoPaten.JovoVerse.model;

// Kelas ini berfungsi sebagai DTO untuk menggabungkan data Transaksi dan Item
public class TransaksiDetail {
    private Transaksi transaksi;
    private itemLibrary item;

    public TransaksiDetail(Transaksi transaksi, itemLibrary item) {
        this.transaksi = transaksi;
        this.item = item;
    }

    // Getters and Setters
    public Transaksi getTransaksi() {
        return transaksi;
    }

    public void setTransaksi(Transaksi transaksi) {
        this.transaksi = transaksi;
    }

    public itemLibrary getItem() {
        return item;
    }

    public void setItem(itemLibrary item) {
        this.item = item;
    }
}