package com.JoyoPaten.JovoVerse.model;

public class Buku extends itemLibrary {
     private String isbn;
    
    public Buku(String idItem, String judul, Integer tahunTerbit, 
                String penulis, Integer halaman,String cover,Integer stok, String isbn) {
        super(idItem, judul, tahunTerbit, penulis, halaman,cover,stok);
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
            idItem != null ? idItem : "N/A",
            judul != null ? judul : "N/A", 
            penulis != null ? penulis : "N/A",
            tahunTerbit != null ? tahunTerbit : 0,
            isbn != null ? isbn : "N/A",
            halaman != null ? halaman : 0,
            stok != null ? stok : "N/A"
        );
    }
    
    @Override
    public String toString() {
        return "Buku{" +
                "idItem='" + idItem + '\'' +
                ", judul='" + judul + '\'' +
                ", isbn='" + isbn + '\'' +
                ", stok=" + stok +
                '}';
    }
}
