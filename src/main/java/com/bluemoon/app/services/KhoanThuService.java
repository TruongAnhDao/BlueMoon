package com.bluemoon.app.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bluemoon.app.models.KhoanThuModel;

public class KhoanThuService {

    // 1. Lấy toàn bộ danh sách khoản thu
    public List<KhoanThuModel> getAllKhoanThu() {
        List<KhoanThuModel> list = new ArrayList<>();
        String query = "SELECT * FROM khoan_thu";
        
        ResultSet rs = null;
        try {
            // Sử dụng hàm tiện ích executeQuery từ MysqlConnection
            rs = MysqlConnection.executeQuery(query);
            
            while (rs.next()) {
                KhoanThuModel model = new KhoanThuModel(
                    rs.getInt("maKhoanThu"),
                    rs.getString("tenKhoanThu"),
                    rs.getDouble("soTien"),
                    rs.getInt("loaiKhoanThu")
                );
                list.add(model);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách khoản thu: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng kết nối thủ công như hướng dẫn trong MysqlConnection.java
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    // 2. Thêm khoản thu mới
    public boolean addKhoanThu(KhoanThuModel khoanThu) {
        String query = "INSERT INTO khoan_thu (tenKhoanThu, soTien, loaiKhoanThu) VALUES (?, ?, ?)";
        
        try {
            // Sử dụng executeUpdate để code gọn hơn và tự động đóng kết nối
            int rowsAffected = MysqlConnection.executeUpdate(query, 
                khoanThu.getTenKhoanThu(), 
                khoanThu.getSoTien(), 
                khoanThu.getLoaiKhoanThu()
            );
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm khoản thu: " + e.getMessage());
            return false;
        }
    }

    // 3. Xóa khoản thu
    public boolean deleteKhoanThu(int maKhoanThu) {
        // Lưu ý: Nếu khoản thu đã có trong bảng 'nop_tien', lệnh này có thể lỗi do ràng buộc khóa ngoại (Foreign Key).
        // Cần xóa dữ liệu bên bảng nop_tien trước nếu có.
        String query = "DELETE FROM khoan_thu WHERE maKhoanThu = ?";
        
        try {
            int rowsAffected = MysqlConnection.executeUpdate(query, maKhoanThu);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa khoản thu (có thể do ràng buộc dữ liệu): " + e.getMessage());
            return false;
        }
    }

    // 4. Tìm kiếm khoản thu theo tên
    public List<KhoanThuModel> searchKhoanThu(String keyword) {
        List<KhoanThuModel> list = new ArrayList<>();
        String query = "SELECT * FROM khoan_thu WHERE tenKhoanThu LIKE ?";
        
        ResultSet rs = null;
        try {
            // Truyền tham số vào executeQuery
            rs = MysqlConnection.executeQuery(query, "%" + keyword + "%");

            while (rs.next()) {
                KhoanThuModel model = new KhoanThuModel(
                    rs.getInt("maKhoanThu"),
                    rs.getString("tenKhoanThu"),
                    rs.getDouble("soTien"),
                    rs.getInt("loaiKhoanThu")
                );
                list.add(model);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
    public int getSoLuongKhoanThu() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM khoan_thu";
        ResultSet rs = null;
        try {
            rs = MysqlConnection.executeQuery(query);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {}
            }
        }
        return count;
    }
}
