package com.JoyoPaten.JovoVerse.model;

public class Buku extends itemLibrary {
     private String isbn;
    
    public Buku(String idItem, String judul, Integer tahunTerbit, 
                String penulis, Integer halaman, String cover,Integer stok, String isbn) {
        super(idItem, judul, tahunTerbit, penulis, halaman, cover, stok);
        this.isbn = isbn;
    }
    
    // Getter & Setter
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    // Implementation of abstract method getInfo()
    @Override
   public String getInfo() {
    return String.format(
        "BUKU - ID: %s | Judul: %s | Penulis: %s | Tahun: %d | ISBN: %s | Halaman: %d | Stok: %d",
        getIdItem() != null ? getIdItem() : "N/A",
        getJudul() != null ? getJudul() : "N/A", 
        getPenulis() != null ? getPenulis() : "N/A",
        getTahunTerbit() != null ? getTahunTerbit() : 0,
        isbn != null ? isbn : "N/A",
        getHalaman() != null ? getHalaman() : 0,
        getStok()
    );
}
    
    @Override
    public String toString() {
        return "Buku{" +
                "idItem='" + idItem + '\'' +
                ", judul='" + judul + '\'' +
                ", isbn='" + isbn + '\'' +
                ", stok=" + stok +
                ", stok=" + stok +
                '}';
    }
}
