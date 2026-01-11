package com.bluemoon.app.models;

public class KhoanThuModel {
    private int maKhoanThu;
    private String tenKhoanThu;
    private double soTien;
    private int loaiKhoanThu;

    // --- CONSTRUCTORS ---

    // Constructor rỗng (Good practice)
    public KhoanThuModel() {
    }

    // Constructor đầy đủ tham số (Được dùng trong hàm loadDummyData của Controller)
    public KhoanThuModel(int maKhoanThu, String tenKhoanThu, double soTien, int loaiKhoanThu) {
        this.maKhoanThu = maKhoanThu;
        this.tenKhoanThu = tenKhoanThu;
        this.soTien = soTien;
        this.loaiKhoanThu = loaiKhoanThu;
    }

    // --- GETTERS AND SETTERS ---
    // Lưu ý: JavaFX PropertyValueFactory dựa vào quy tắc đặt tên chuẩn get<TênBiến> 
    // để tìm dữ liệu. Ví dụ: "maKhoanThu" -> tìm hàm getMaKhoanThu()

    public int getMaKhoanThu() {
        return maKhoanThu;
    }

    public void setMaKhoanThu(int maKhoanThu) {
        this.maKhoanThu = maKhoanThu;
    }

    public String getTenKhoanThu() {
        return tenKhoanThu;
    }

    public void setTenKhoanThu(String tenKhoanThu) {
        this.tenKhoanThu = tenKhoanThu;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public int getLoaiKhoanThu() {
        return loaiKhoanThu;
    }

    public void setLoaiKhoanThu(int loaiKhoanThu) {
        this.loaiKhoanThu = loaiKhoanThu;
    }

    // (Tùy chọn) Override toString để debug dễ hơn
    @Override
    public String toString() {
        return tenKhoanThu;
    }
}