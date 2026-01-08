package com.bluemoon.app.models;

import java.util.Date;

public class ThuPhiModel {
    private int idNopTien;
    private int maHo;
    private int maKhoanThu;
    private String tenKhoanThu; // Dùng để hiển thị tên từ bảng khoan_thu
    private double soTienNop;
    private Date ngayNop;
    private String nguoiNop;

    // Constructor không tham số
    public ThuPhiModel() {}

    // Các phương thức SET (đây chính là nơi các hàm "set" xuất hiện)
    public void setIdNopTien(int idNopTien) { this.idNopTien = idNopTien; }
    public void setMaHo(int maHo) { this.maHo = maHo; }
    public void setMaKhoanThu(int maKhoanThu) { this.maKhoanThu = maKhoanThu; }
    public void setTenKhoanThu(String tenKhoanThu) { this.tenKhoanThu = tenKhoanThu; }
    public void setSoTienNop(double soTienNop) { this.soTienNop = soTienNop; }
    public void setNgayNop(Date ngayNop) { this.ngayNop = ngayNop; }
    public void setNguoiNop(String nguoiNop) { this.nguoiNop = nguoiNop; }

    // Các phương thức GET (Dùng để lấy dữ liệu hiển thị lên TableView)
    public int getIdNopTien() { return idNopTien; }
    public int getMaHo() { return maHo; }
    public int getMaKhoanThu() { return maKhoanThu; }
    public String getTenKhoanThu() { return tenKhoanThu; }
    public double getSoTienNop() { return soTienNop; }
    public Date getNgayNop() { return ngayNop; }
    public String getNguoiNop() { return nguoiNop; }
}