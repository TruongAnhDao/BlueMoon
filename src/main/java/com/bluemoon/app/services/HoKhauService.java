package com.bluemoon.app.services;

import com.bluemoon.app.models.HoKhauModel;
import com.bluemoon.app.models.NhanKhauModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    // Lấy danh sách nhân khẩu để chọn làm chủ hộ
    public static List<NhanKhauModel> getAllNhanKhau() {
        List<NhanKhauModel> list = new ArrayList<>();
        String sql = "SELECT id, hoTen, ngaySinh, cccd FROM nhan_khau";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql);
            while (rs.next()) {
                list.add(new NhanKhauModel(
                    rs.getInt("id"),
                    rs.getString("hoTen"),
                    rs.getDate("ngaySinh"),
                    rs.getString("cccd")
                ));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) { e.printStackTrace(); }
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

    // Thêm hộ khẩu mới (Kỹ thuật Transaction)
    public static boolean addHoKhau(HoKhauModel hoKhau, int idChuHo) {
        Connection conn = MysqlConnection.getConnection();
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        
        try {
            // Tắt chế độ tự động lưu để quản lý giao dịch
            conn.setAutoCommit(false);

            // Thêm vào bảng ho_khau
            String sql1 = "INSERT INTO ho_khau (maHoKhau, diaChi, soThanhVien) VALUES (?, ?, ?)";
            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setInt(1, hoKhau.getMaHoKhau());
            pstmt1.setString(2, hoKhau.getDiaChi());
            pstmt1.setInt(3, 1); // Mới tạo thì có 1 thành viên là chủ hộ
            pstmt1.executeUpdate();

            // Thêm vào bảng chu_ho
            String sql2 = "INSERT INTO chu_ho (idNhanKhau, maHoKhau) VALUES (?, ?)";
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, idChuHo);
            pstmt2.setInt(2, hoKhau.getMaHoKhau());
            pstmt2.executeUpdate();

            // Thêm vào bảng quan_he (Xác nhận người này là Chủ hộ)
            String sql3 = "INSERT INTO quan_he (idNhanKhau, maHoKhau, quanHeVoiChuHo) VALUES (?, ?, ?)";
            pstmt3 = conn.prepareStatement(sql3);
            pstmt3.setInt(1, idChuHo);
            pstmt3.setInt(2, hoKhau.getMaHoKhau());
            pstmt3.setString(3, "Chủ hộ");
            pstmt3.executeUpdate();

            // Nếu cả 3 bước ok thì mới lưu thật
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Gặp lỗi thì hoàn tác
            } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (pstmt3 != null) pstmt3.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}