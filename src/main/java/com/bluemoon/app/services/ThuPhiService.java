package com.bluemoon.app.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.bluemoon.app.models.ThuPhiModel; // Giả định bạn đã tạo Model này

public class ThuPhiService {

    /**
     * Lấy toàn bộ lịch sử thu phí từ database.
     * Cần JOIN với bảng khoan_thu để lấy tên khoản thu hiển thị lên giao diện.
     */
    public static List<ThuPhiModel> getAllPaymentHistory() {
        List<ThuPhiModel> list = new ArrayList<>();
        // Truy vấn JOIN để lấy thông tin từ cả 2 bảng nop_tien và khoan_thu
        String sql = "SELECT n.*, k.tenKhoanThu " +
                     "FROM nop_tien n " +
                     "JOIN khoan_thu k ON n.maKhoanThu = k.maKhoanThu " +
                     "ORDER BY n.ngayNop DESC";
        
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql);
            while (rs.next()) {
                ThuPhiModel item = new ThuPhiModel();
                item.setIdNopTien(rs.getInt("idNopTien"));
                item.setMaHo(rs.getInt("maHo"));
                item.setMaKhoanThu(rs.getInt("maKhoanThu"));
                item.setTenKhoanThu(rs.getString("tenKhoanThu")); // Lấy từ bảng khoan_thu
                item.setSoTienNop(rs.getDouble("soTienNop"));
                item.setNgayNop(rs.getDate("ngayNop"));
                item.setNguoiNop(rs.getString("nguoiNop"));
                
                list.add(item);
            }
            // Đóng kết nối sau khi truy vấn xong
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thêm một bản ghi nộp tiền mới vào bảng nop_tien.
     */
    public static boolean addPayment(ThuPhiModel payment) {
        String sql = "INSERT INTO nop_tien (maKhoanThu, maHo, soTienNop, ngayNop, nguoiNop) VALUES (?, ?, ?, ?, ?)";
        try {
            int rows = MysqlConnection.executeUpdate(sql, 
                payment.getMaKhoanThu(), 
                payment.getMaHo(), 
                payment.getSoTienNop(), 
                new java.sql.Date(payment.getNgayNop().getTime()), 
                payment.getNguoiNop()
            );
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một bản ghi lịch sử thu phí dựa trên ID.
     */
    public static boolean deletePayment(int idNopTien) {
        String sql = "DELETE FROM nop_tien WHERE idNopTien = ?";
        try {
            int rows = MysqlConnection.executeUpdate(sql, idNopTien);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm kiếm lịch sử thu phí theo mã hộ dân.
     */
    public static List<ThuPhiModel> searchByMaHo(String maHo) {
        List<ThuPhiModel> list = new ArrayList<>();
        String sql = "SELECT n.*, k.tenKhoanThu " +
                     "FROM nop_tien n " +
                     "JOIN khoan_thu k ON n.maKhoanThu = k.maKhoanThu " +
                     "WHERE n.maHo LIKE ? " +
                     "ORDER BY n.ngayNop DESC";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql, "%" + maHo + "%");
            while (rs.next()) {
                ThuPhiModel item = new ThuPhiModel();
                item.setIdNopTien(rs.getInt("idNopTien"));
                item.setMaHo(rs.getInt("maHo"));
                item.setMaKhoanThu(rs.getInt("maKhoanThu"));
                item.setTenKhoanThu(rs.getString("tenKhoanThu"));
                item.setSoTienNop(rs.getDouble("soTienNop"));
                item.setNgayNop(rs.getDate("ngayNop"));
                item.setNguoiNop(rs.getString("nguoiNop"));
                list.add(item);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}