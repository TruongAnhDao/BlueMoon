package com.bluemoon.app.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ThongKeService {

    // 1. Thống kê Nhân khẩu theo Giới tính
    public static Map<String, Integer> getNhanKhauByGioiTinh() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT gioiTinh, COUNT(*) as soLuong FROM nhan_khau GROUP BY gioiTinh";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql);
            while (rs.next()) {
                String gioiTinh = rs.getString("gioiTinh");
                int soLuong = rs.getInt("soLuong");
                data.put(gioiTinh, soLuong);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    // 2. Thống kê Nhân khẩu theo Độ tuổi (Chia nhóm: Trẻ em, Lao động, Người cao tuổi)
    public static Map<String, Integer> getNhanKhauByDoTuoi() {
        Map<String, Integer> data = new HashMap<>();
        // Logic: Tính tuổi bằng (YEAR(NOW()) - YEAR(ngaySinh))
        String sql = "SELECT " +
                     "SUM(CASE WHEN (YEAR(CURDATE()) - YEAR(ngaySinh)) < 18 THEN 1 ELSE 0 END) AS Duoi18, " +
                     "SUM(CASE WHEN (YEAR(CURDATE()) - YEAR(ngaySinh)) BETWEEN 18 AND 60 THEN 1 ELSE 0 END) AS Tu18Den60, " +
                     "SUM(CASE WHEN (YEAR(CURDATE()) - YEAR(ngaySinh)) > 60 THEN 1 ELSE 0 END) AS Tren60 " +
                     "FROM nhan_khau";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql);
            if (rs.next()) {
                data.put("Dưới 18 tuổi", rs.getInt("Duoi18"));
                data.put("18 - 60 tuổi", rs.getInt("Tu18Den60"));
                data.put("Trên 60 tuổi", rs.getInt("Tren60"));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    // 3. Thống kê Tổng thu nhập theo từng Khoản thu
    public static Map<String, Double> getTongThuTungKhoan() {
        Map<String, Double> data = new HashMap<>();
        // Join bảng khoan_thu và nop_tien để tính tổng
        String sql = "SELECT k.tenKhoanThu, SUM(n.soTienNop) as tongTien " +
                     "FROM nop_tien n " +
                     "JOIN khoan_thu k ON n.maKhoanThu = k.maKhoanThu " +
                     "GROUP BY k.tenKhoanThu";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql);
            while (rs.next()) {
                data.put(rs.getString("tenKhoanThu"), rs.getDouble("tongTien"));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }
    
    // 4. Thống kê Tạm trú / Tạm vắng
    public static Map<String, Integer> getTamTruTamVang() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT trangThai, COUNT(*) as soLuong FROM tam_tru_tam_vang GROUP BY trangThai";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql);
            while (rs.next()) {
                int tt = rs.getInt("trangThai"); // 0: Tạm trú, 1: Tạm vắng
                String label = (tt == 0) ? "Tạm trú" : "Tạm vắng";
                data.put(label, rs.getInt("soLuong"));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }
}