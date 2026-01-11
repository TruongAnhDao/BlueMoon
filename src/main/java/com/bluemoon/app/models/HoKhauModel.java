package com.bluemoon.app.models;

public class HoKhauModel {
    private int maHoKhau;
    private String tenChuHo; // Lấy từ bảng nhan_khau thông qua bảng chu_ho
    private String diaChi;
    private int soThanhVien;

    public HoKhauModel() {
    }

    public HoKhauModel(int maHoKhau, String tenChuHo, String diaChi, int soThanhVien) {
        this.maHoKhau = maHoKhau;
        this.tenChuHo = tenChuHo;
        this.diaChi = diaChi;
        this.soThanhVien = soThanhVien;
    }

    // Getters and Setters
    public int getMaHoKhau() { return maHoKhau; }
    public void setMaHoKhau(int maHoKhau) { this.maHoKhau = maHoKhau; }

    public String getTenChuHo() { return tenChuHo; }
    public void setTenChuHo(String tenChuHo) { this.tenChuHo = tenChuHo; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public int getSoThanhVien() { return soThanhVien; }
    public void setSoThanhVien(int soThanhVien) { this.soThanhVien = soThanhVien; }
}