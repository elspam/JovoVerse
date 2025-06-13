package com.JoyoPaten.JovoVerse.model;

public class Jurnal extends itemLibrary {
    private Integer volume;
    private Integer noEdisi;
    private String issn;
    
    // Constructors
    public Jurnal() {}
    
    public Jurnal(String idItem, String judul, Integer tahunTerbit, String penulis, 
                  Integer halaman, String cover,Integer stok, Integer volume, Integer noEdisi, String issn) {
        super(idItem, judul, tahunTerbit, penulis, halaman, cover, stok);
        this.volume = volume;
        this.noEdisi = noEdisi;
        this.issn = issn;
    }
    
    // Getters & Setters
    public Integer getVolume() { return volume; }
    public void setVolume(Integer volume) { this.volume = volume; }
    
    public Integer getNoEdisi() { return noEdisi; }
    public void setNoEdisi(Integer noEdisi) { this.noEdisi = noEdisi; }
    
    public String getIssn() { return issn; }
    public void setIssn(String issn) { this.issn = issn; }
    
    // Implementation of abstract method getInfo()
    @Override
    public String getInfo() {
        return String.format(
            "JURNAL - ID: %s | Judul: %s | Penulis: %s | Tahun: %d | ISSN: %s | Vol: %d | Ed: %d | Halaman: %d | Stok: %s",
            idItem != null ? idItem : "N/A",
            judul != null ? judul : "N/A",
            penulis != null ? penulis : "N/A", 
            tahunTerbit != null ? tahunTerbit : 0,
            issn != null ? issn : "N/A",
            volume != null ? volume : 0,
            noEdisi != null ? noEdisi : 0,
            halaman != null ? halaman : 0,
            stok != 0 ? stok : "N/A"
        );
    }
    
    @Override
    public String toString() {
        return "Jurnal{" +
                "idItem='" + idItem + '\'' +
                ", judul='" + judul + '\'' +
                ", issn='" + issn + '\'' +
                ", volume=" + volume +
                ", noEdisi=" + noEdisi +
                ", stok=" + stok +
                ", stok=" + stok +
                '}';
    }
}
