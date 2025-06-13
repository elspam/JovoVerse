package com.JoyoPaten.JovoVerse.model;

import java.sql.Date;

public class Transaksi {
    private String username;
    private String idItem;
    private Date tanggalPinjam;
    private Date tanggalKembali;
    private Date deadline;
    private Integer denda;
    private Integer status;

    public Transaksi() {}

    public Transaksi(String username, String idItem, Date tanggalPinjam, Date tanggalKembali, Date deadline, Integer denda,Integer status ) {
        this.username = username;
        this.idItem = idItem;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.deadline = deadline;
        this.denda = denda;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public Date getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(Date tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public Date getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(Date tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getDenda() {
        return denda;
    }

    public void setDenda(Integer denda) {
        this.denda = denda;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

