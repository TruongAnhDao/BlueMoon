package com.bluemoon.app.controllers;

import java.io.IOException;

import com.bluemoon.app.services.LoginService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField pfPassword;

    // --- 1. Xử lý Đăng nhập (Giữ nguyên) ---
    @FXML
    public void handleLogin(ActionEvent event) {
        String user = tfUsername.getText();
        String pass = pfPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        boolean isLoginSuccess = LoginService.checkLogin(user, pass);

        if (isLoginSuccess) {
            System.out.println("Đăng nhập thành công: " + user);
            changeScene(event, "/com/bluemoon/views/Home.fxml"); // Chuyển sang trang chủ
        } else {
            showAlert("Lỗi đăng nhập", "Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    // --- 2. Xử lý Đăng ký (Mới thêm) ---
    @FXML
    public void handleRegister(ActionEvent event) {
        System.out.println("Chuyển sang màn hình Đăng ký...");
        // Giả sử file giao diện đăng ký tên là Register.fxml
        changeScene(event, "/com/bluemoon/views/Register.fxml");
    }

    // --- 3. Xử lý Quên mật khẩu 
    @FXML
    public void handleForgotPassword(ActionEvent event) {
        System.out.println("Chuyển sang màn hình Quên mật khẩu...");
        // Giả sử file giao diện quên mật khẩu tên là ForgotPassword.fxml
        changeScene(event, "/com/bluemoon/views/ForgotPassword.fxml");
    }

    // --- Hàm tiện ích chuyển cảnh (Dùng chung) ---
    private void changeScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Lấy Stage hiện tại từ nút bấm
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Hiển thị lỗi rõ ràng hơn để biết thiếu file nào
            showAlert("Lỗi hệ thống", "Không tìm thấy file giao diện: " + fxmlPath + "\nVui lòng kiểm tra lại tên file!");
        }
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("BlueMoon Management");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}