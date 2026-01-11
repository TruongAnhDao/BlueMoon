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
        
        String sql = "SELECT " +
                     "   hk.maHoKhau, " +
                     "   hk.diaChi, " +
                     "   (SELECT COUNT(*) FROM quan_he qh WHERE qh.maHoKhau = hk.maHoKhau) AS soThanhVien, " +
                     "   nk.hoTen AS tenChuHo " +
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
                    rs.getInt("soThanhVien") // Lấy giá trị từ cột tính toán ở trên
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

    // Lấy ID của chủ hộ hiện tại dựa vào mã hộ
    public static int getChuHoIdByMaHo(int maHoKhau) {
        String sql = "SELECT idNhanKhau FROM chu_ho WHERE maHoKhau = ?";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql, maHoKhau);
            if (rs.next()) {
                int id = rs.getInt("idNhanKhau");
                rs.getStatement().getConnection().close();
                return id;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public static List<NhanKhauModel> getMembersOfHoKhau(int maHoKhau) {
        List<NhanKhauModel> list = new ArrayList<>();
        // Join bảng nhan_khau với quan_he để lọc người thuộc hộ này
        String sql = "SELECT nk.id, nk.hoTen, nk.ngaySinh, nk.cccd, qh.quanHeVoiChuHo " +
                     "FROM nhan_khau nk " +
                     "JOIN quan_he qh ON nk.id = qh.idNhanKhau " +
                     "WHERE qh.maHoKhau = ?";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql, maHoKhau);
            while (rs.next()) {
                NhanKhauModel nk = new NhanKhauModel();
                nk.setId(rs.getInt("id"));
                nk.setHoTen(rs.getString("hoTen"));
                nk.setNgaySinh(rs.getDate("ngaySinh"));
                nk.setCccd(rs.getString("cccd"));
                nk.setQuanHeVoiChuHo(rs.getString("quanHeVoiChuHo"));
                
                list.add(nk);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isChuHo(int idNhanKhau, int maHoKhau) {
        String sql = "SELECT * FROM chu_ho WHERE idNhanKhau = ? AND maHoKhau = ?";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql, idNhanKhau, maHoKhau);
            boolean isOwner = rs.next();
            rs.getStatement().getConnection().close();
            return isOwner;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    // Cập nhật thông tin hộ khẩu
    public static boolean updateHoKhau(int maHoKhau, String diaChiMoi, int idChuHoMoi) {
        Connection conn = MysqlConnection.getConnection();
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;

        try {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật địa chỉ trong bảng ho_khau
            String sql1 = "UPDATE ho_khau SET diaChi = ? WHERE maHoKhau = ?";
            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setString(1, diaChiMoi);
            pstmt1.setInt(2, maHoKhau);
            pstmt1.executeUpdate();

            // 2. Kiểm tra xem chủ hộ có thay đổi không
            int currentChuHoId = getChuHoIdByMaHo(maHoKhau);
            if (currentChuHoId != idChuHoMoi) {
                // A. Cập nhật bảng chu_ho
                String sql2 = "UPDATE chu_ho SET idNhanKhau = ? WHERE maHoKhau = ?";
                pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setInt(1, idChuHoMoi);
                pstmt2.setInt(2, maHoKhau);
                pstmt2.executeUpdate();

                // B. Hạ chủ hộ cũ xuống thành "Thành viên" (trong bảng quan_he)
                if (currentChuHoId != -1) {
                    String sql3 = "UPDATE quan_he SET quanHeVoiChuHo = ? WHERE idNhanKhau = ? AND maHoKhau = ?";
                    pstmt3 = conn.prepareStatement(sql3);
                    pstmt3.setString(1, "Thành viên"); // Hoặc quan hệ khác tùy nghiệp vụ
                    pstmt3.setInt(2, currentChuHoId);
                    pstmt3.setInt(3, maHoKhau);
                    pstmt3.executeUpdate();
                }

                // C. Thăng cấp chủ hộ mới lên "Chủ hộ" (trong bảng quan_he)
                // Lưu ý: Nếu người mới chưa có trong quan_he thì phải INSERT, nếu có rồi thì UPDATE.
                // Ở đây giả định người mới đã là thành viên trong hộ. Nếu chưa, cần logic phức tạp hơn (MoveIn).
                // Ta dùng cú pháp "ON DUPLICATE KEY UPDATE" hoặc Check trước. 
                // Để đơn giản cho bài tập này: Ta Update thẳng.
                String sql4 = "UPDATE quan_he SET quanHeVoiChuHo = 'Chủ hộ' WHERE idNhanKhau = ? AND maHoKhau = ?";
                pstmt4 = conn.prepareStatement(sql4);
                pstmt4.setInt(1, idChuHoMoi);
                pstmt4.setInt(2, maHoKhau);
                int rows = pstmt4.executeUpdate();
                
                // Nếu update không được (do người này chưa từng ở trong hộ), ta Insert mới vào quan hệ
                if (rows == 0) {
                     String sqlInsert = "INSERT INTO quan_he (idNhanKhau, maHoKhau, quanHeVoiChuHo) VALUES (?, ?, 'Chủ hộ')";
                     try (PreparedStatement pInsert = conn.prepareStatement(sqlInsert)) {
                         pInsert.setInt(1, idChuHoMoi);
                         pInsert.setInt(2, maHoKhau);
                         pInsert.executeUpdate();
                     }
                }
            }

            conn.commit(); // Xác nhận lưu
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (pstmt3 != null) pstmt3.close();
                if (pstmt4 != null) pstmt4.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Xóa thành viên khỏi hộ khẩu (Xóa khỏi bảng quan_he)
    public static boolean removeMember(int idNhanKhau, int maHoKhau) {
        // Không cho phép xóa Chủ hộ trực tiếp (Phải đổi chủ hộ trước)
        if (isChuHo(idNhanKhau, maHoKhau)) {
            return false; 
        }

        String sql = "DELETE FROM quan_he WHERE idNhanKhau = ? AND maHoKhau = ?";
        try {
            int rows = MysqlConnection.executeUpdate(sql, idNhanKhau, maHoKhau);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Thêm thành viên vào hộ khẩu
    public static boolean addMember(int idNhanKhau, int maHoKhau, String quanHe) {
        // Kiểm tra xem người này đã có trong hộ chưa để tránh trùng
        String checkSql = "SELECT * FROM quan_he WHERE idNhanKhau = ? AND maHoKhau = ?";
        try {
            ResultSet rs = MysqlConnection.executeQuery(checkSql, idNhanKhau, maHoKhau);
            if (rs.next()) {
                rs.getStatement().getConnection().close();
                return false; // Đã tồn tại
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) { e.printStackTrace(); }

        String sql = "INSERT INTO quan_he (idNhanKhau, maHoKhau, quanHeVoiChuHo) VALUES (?, ?, ?)";
        try {
            int rows = MysqlConnection.executeUpdate(sql, idNhanKhau, maHoKhau, quanHe);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}