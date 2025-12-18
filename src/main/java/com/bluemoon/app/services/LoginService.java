package com.bluemoon.app.services;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {
    public static boolean checkLogin(String user, String pass) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            ResultSet rs = MysqlConnection.executeQuery(sql, user, pass);
            boolean exists = rs.next(); // Nếu có dòng dữ liệu trả về thì đúng
            rs.getStatement().getConnection().close(); 
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}