package com.example.user.catatanku.Model;

public class ModelCatatan {

    private String id_catatan,id_user,judul,isi,foto,tgl_simpan;

    public ModelCatatan(){}

    public String getId_catatan() {
        return id_catatan;
    }

    public void setId_catatan(String id_catatan) {
        this.id_catatan = id_catatan;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getTgl_simpan() {
        return tgl_simpan;
    }

    public void setTgl_simpan(String tgl_simpan) {
        this.tgl_simpan = tgl_simpan;
    }
}
