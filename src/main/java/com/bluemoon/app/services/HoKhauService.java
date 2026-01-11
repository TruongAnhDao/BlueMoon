package com.bluemoon.app.services;

import com.bluemoon.app.models.HoKhauModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HoKhauService {

    // Lấy danh sách tất cả hộ khẩu kèm tên chủ hộ
    public static List<HoKhauModel> getAllHoKhau() {
        List<HoKhauModel> list = new ArrayList<>();
        // Query: Kết nối bảng ho_khau -> chu_ho -> nhan_khau để lấy tên chủ hộ
        String sql = "SELECT hk.maHoKhau, hk.diaChi, hk.soThanhVien, nk.hoTen AS tenChuHo " +
                     "FROM ho_khau hk " +
                     "LEFT JOIN chu_ho ch ON hk.maHoKhau = ch.maHoKhau " +
                     "LEFT JOIN nhan_khau nk ON ch.idNhanKhau = nk.id " +
                     "ORDER BY hk.maHoKhau ASC";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql);
            while (rs.next()) {
                HoKhauModel hk = new HoKhauModel(
                    rs.getInt("maHoKhau"),
                    rs.getString("tenChuHo") != null ? rs.getString("tenChuHo") : "Chưa có chủ hộ",
                    rs.getString("diaChi"),
                    rs.getInt("soThanhVien")
                );
                list.add(hk);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm kiếm theo mã hộ hoặc tên chủ hộ
    public static List<HoKhauModel> searchHoKhau(String keyword) {
        List<HoKhauModel> list = new ArrayList<>();
        String sql = "SELECT hk.maHoKhau, hk.diaChi, hk.soThanhVien, nk.hoTen AS tenChuHo " +
                     "FROM ho_khau hk " +
                     "LEFT JOIN chu_ho ch ON hk.maHoKhau = ch.maHoKhau " +
                     "LEFT JOIN nhan_khau nk ON ch.idNhanKhau = nk.id " +
                     "WHERE CAST(hk.maHoKhau AS CHAR) LIKE ? OR nk.hoTen LIKE ?";
        try {
            String searchPattern = "%" + keyword + "%";
            ResultSet rs = MysqlConnection.executeQuery(sql, searchPattern, searchPattern);
            while (rs.next()) {
                list.add(new HoKhauModel(
                    rs.getInt("maHoKhau"),
                    rs.getString("tenChuHo") != null ? rs.getString("tenChuHo") : "Chưa có chủ hộ",
                    rs.getString("diaChi"),
                    rs.getInt("soThanhVien")
                ));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Hàm xóa hộ khẩu (cần cẩn thận vì ràng buộc khóa ngoại với bảng nhan_khau/nop_tien)
    public static boolean deleteHoKhau(int maHoKhau) {
        // Thực tế cần xóa các bảng liên quan trước hoặc dùng ON DELETE CASCADE trong DB
        String sql = "DELETE FROM ho_khau WHERE maHoKhau = ?";
        try {
            int rows = MysqlConnection.executeUpdate(sql, maHoKhau);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}