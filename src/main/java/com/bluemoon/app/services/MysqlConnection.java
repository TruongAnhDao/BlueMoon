package com.bluemoon.app.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/blue_moon_db";
    private static final String USER = "root"; 
    private static final String PASS = "T06012005a@"; // Mật khẩu MySQL của bạn, nếu không có thì để trống ""

    /**
     * Hàm lấy kết nối CSDL
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Đăng ký driver (MySQL 8.0+)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Kết nối CSDL BlueMoon thành công!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
        return conn;
    }

    /**
     * Hàm chạy thử để test kết nối (Nhấn Run để kiểm tra)
     */
    public static void main(String[] args) {
        getConnection();
    }
}