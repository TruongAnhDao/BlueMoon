package com.bluemoon.app.models;

import java.sql.Date;

public class NhanKhauModel {
    private int id;
    private String hoTen;
    private Date ngaySinh;
    private String gioiTinh;
    private String danToc;
    private String tonGiao;
    private String cccd;
    private Date ngayCap;
    private String noiCap;
    private String ngheNghiep;
    private String ghiChu;

    public NhanKhauModel() {}

    public NhanKhauModel(int id, String hoTen, Date ngaySinh, String gioiTinh, String danToc, 
                         String tonGiao, String cccd, Date ngayCap, String noiCap, 
                         String ngheNghiep, String ghiChu) {
        this.id = id;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.danToc = danToc;
        this.tonGiao = tonGiao;
        this.cccd = cccd;
        this.ngayCap = ngayCap;
        this.noiCap = noiCap;
        this.ngheNghiep = ngheNghiep;
        this.ghiChu = ghiChu;
    }

    // Rút gọn để dùng cho chức năng chọn chủ hộ
    public NhanKhauModel(int id, String hoTen, Date ngaySinh, String cccd) {
        this.id = id;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.cccd = cccd;
    }

    // --- GETTERS & SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getDanToc() { return danToc; }
    public void setDanToc(String danToc) { this.danToc = danToc; }

    public String getTonGiao() { return tonGiao; }
    public void setTonGiao(String tonGiao) { this.tonGiao = tonGiao; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public Date getNgayCap() { return ngayCap; }
    public void setNgayCap(Date ngayCap) { this.ngayCap = ngayCap; }

    public String getNoiCap() { return noiCap; }
    public void setNoiCap(String noiCap) { this.noiCap = noiCap; }

    public String getNgheNghiep() { return ngheNghiep; }
    public void setNgheNghiep(String ngheNghiep) { this.ngheNghiep = ngheNghiep; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    @Override
    public String toString() {
        return hoTen + " - " + cccd;
    }
}