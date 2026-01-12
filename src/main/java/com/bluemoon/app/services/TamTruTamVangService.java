package com.bluemoon.app.services;

import java.sql.Date;
import java.sql.SQLException;

public class TamTruTamVangService {

    // Thêm mới bản ghi Tạm trú (0) hoặc Tạm vắng (1)
    public static boolean addTamTruTamVang(int idNhanKhau, int trangThai, String diaChi, Date thoiGian, String noiDung) {
        String sql = "INSERT INTO tam_tru_tam_vang (idNhanKhau, trangThai, diaChi, thoiGian, noiDung) VALUES (?, ?, ?, ?, ?)";
        try {
            int rows = MysqlConnection.executeUpdate(sql, idNhanKhau, trangThai, diaChi, thoiGian, noiDung);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}