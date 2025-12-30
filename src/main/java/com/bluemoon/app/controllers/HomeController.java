package com.bluemoon.app.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class HomeController {

    // 1. Chức năng Đăng xuất
    @FXML
    public void handleLogout(ActionEvent event) {
        System.out.println("Đang đăng xuất...");
        changeScene(event, "/com/bluemoon/views/Login.fxml", "Hệ thống quản lý Blue Moon");
    }

    // 2. Chức năng chuyển sang Quản lý Hộ khẩu 
    @FXML
    public void handleGoToHoKhau(ActionEvent event) {
        System.out.println("Chuyển sang Quản lý hộ khẩu...");
        // changeScene(event, "/com/bluemoon/views/HoKhau.fxml", "Quản lý Hộ khẩu");
    }

    // 3. Chức năng chuyển sang Quản lý Khoản thu
    @FXML
    public void handleGoToKhoanThu(ActionEvent event) {
        System.out.println("Chuyển sang Quản lý khoản thu...");
        // changeScene(event, "/com/bluemoon/views/KhoanThu.fxml", "Quản lý Khoản thu");
    }

    // Hàm tiện ích chuyển cảnh (Tái sử dụng logic từ LoginController)
    private void changeScene(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Không tìm thấy file giao diện: " + fxmlPath);
            alert.show();
        }
    }
}