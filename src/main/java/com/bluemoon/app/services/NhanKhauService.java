package com.bluemoon.app.services;

import com.bluemoon.app.models.NhanKhauModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanKhauService {

    // Lấy toàn bộ danh sách nhân khẩu
    public static List<NhanKhauModel> getAllNhanKhauFull() {
        List<NhanKhauModel> list = new ArrayList<>();
        String sql = "SELECT * FROM nhan_khau ORDER BY id DESC";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql);
            while (rs.next()) {
                list.add(new NhanKhauModel(
                    rs.getInt("id"),
                    rs.getString("hoTen"),
                    rs.getDate("ngaySinh"),
                    rs.getString("gioiTinh"),
                    rs.getString("danToc"),
                    rs.getString("tonGiao"),
                    rs.getString("cccd"),
                    rs.getDate("ngayCap"),
                    rs.getString("noiCap"),
                    rs.getString("ngheNghiep"),
                    rs.getString("ghiChu")
                ));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm nhân khẩu mới
    public static boolean addNhanKhau(NhanKhauModel nk) {
        String sql = "INSERT INTO nhan_khau (hoTen, ngaySinh, gioiTinh, danToc, tonGiao, cccd, ngayCap, noiCap, ngheNghiep, ghiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int rows = MysqlConnection.executeUpdate(sql,
                nk.getHoTen(),
                nk.getNgaySinh(),
                nk.getGioiTinh(),
                nk.getDanToc(),
                nk.getTonGiao(),
                nk.getCccd(),
                nk.getNgayCap(),
                nk.getNoiCap(),
                nk.getNgheNghiep(),
                nk.getGhiChu()
            );
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa nhân khẩu
    public static boolean deleteNhanKhau(int id) {
        // Lưu ý: Cần xử lý ràng buộc khóa ngoại nếu nhân khẩu này là chủ hộ hoặc có quan hệ
        String sql = "DELETE FROM nhan_khau WHERE id = ?";
        try {
            int rows = MysqlConnection.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Tìm kiếm nhân khẩu theo tên hoặc CCCD
    public static List<NhanKhauModel> searchNhanKhau(String keyword) {
        List<NhanKhauModel> list = new ArrayList<>();
        String sql = "SELECT * FROM nhan_khau WHERE hoTen LIKE ? OR cccd LIKE ?";
        try {
            String query = "%" + keyword + "%";
            ResultSet rs = MysqlConnection.executeQuery(sql, query, query);
            while (rs.next()) {
                list.add(new NhanKhauModel(
                    rs.getInt("id"),
                    rs.getString("hoTen"),
                    rs.getDate("ngaySinh"),
                    rs.getString("gioiTinh"),
                    rs.getString("danToc"),
                    rs.getString("tonGiao"),
                    rs.getString("cccd"),
                    rs.getDate("ngayCap"),
                    rs.getString("noiCap"),
                    rs.getString("ngheNghiep"),
                    rs.getString("ghiChu")
                ));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}