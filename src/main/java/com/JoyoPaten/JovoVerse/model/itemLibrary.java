package com.JoyoPaten.JovoVerse.model;

public abstract class itemLibrary {
    protected String idItem;
    protected String judul;
    protected Integer tahunTerbit;
    protected String penulis;
    protected Integer halaman;
    protected String cover;
    protected Integer stok;
    
    // Constructors
    public itemLibrary() {}
    
    public itemLibrary(String idItem, String judul, Integer tahunTerbit, 
                      String penulis, Integer halaman, String cover,Integer stok) {
        this.idItem = idItem;
        this.judul = judul;
        this.tahunTerbit = tahunTerbit;
        this.penulis = penulis;
        this.halaman = halaman;
        this.cover = cover;
        this.stok = stok;
    }
    
    // Getters & Setters
    public String getIdItem() { return idItem; }
    public void setIdItem(String idItem) { this.idItem = idItem; }
    
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    
    public Integer getTahunTerbit() { return tahunTerbit; }
    public void setTahunTerbit(Integer tahunTerbit) { this.tahunTerbit = tahunTerbit; }
    
    public String getPenulis() { return penulis; }
    public void setPenulis(String penulis) { this.penulis = penulis; }
    
    public Integer getHalaman() { return halaman; }
    public void setHalaman(Integer halaman) { this.halaman = halaman; }
    
    
    public String getCover() {
        return cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    public Integer getStok() { return stok; }
    public void setStok(Integer stok) { this.stok = stok; }
    
    // Abstract method - harus diimplementasi oleh child classes
    public abstract String getInfo();
}
