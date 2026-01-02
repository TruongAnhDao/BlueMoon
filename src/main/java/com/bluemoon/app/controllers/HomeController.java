package com.bluemoon.app.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    // Liên kết với fx:id="mainBorderPane" trong file Dashboard.fxml
    @FXML
    private BorderPane mainBorderPane; 

    // Biến để lưu giao diện "Chào mừng" ban đầu, giúp quay lại trang chủ nhanh mà không cần load lại file
    private Parent homeView; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khi controller khởi chạy, lưu lại cái phần "Chào mừng" đang nằm ở giữa màn hình
        if (mainBorderPane != null) {
            homeView = (Parent) mainBorderPane.getCenter();
        }
    }

    // --- CÁC CHỨC NĂNG MENU (Giữ nguyên Sidebar, chỉ đổi phần giữa) ---

    // 1. Nút Trang chủ
    @FXML
    public void handleGoToHome(ActionEvent event) {
        if (homeView != null) {
            mainBorderPane.setCenter(homeView); // Trả lại giao diện chào mừng cũ
        }
    }

    // 2. Nút Quản lý Hộ khẩu
    @FXML
    public void handleGoToHoKhau(ActionEvent event) {
        System.out.println("Chuyển tab: Quản lý hộ khẩu");
        // Gọi hàm load phần giữa, trỏ đúng đường dẫn file HouseholdView.fxml bạn vừa tạo
        loadCenterView("/com/bluemoon/app/views/HouseholdView.fxml"); 
    }

    // 3. Nút Quản lý Khoản thu
    @FXML
    public void handleGoToKhoanThu(ActionEvent event) {
        System.out.println("Chuyển tab: Quản lý khoản thu");
        // loadCenterView("/com/bluemoon/app/views/KhoanThuView.fxml"); // (Mở comment khi bạn đã tạo file này)
    }

    // 4. Nút Thống kê
    @FXML
    public void handleGoToThongKe(ActionEvent event) {
        System.out.println("Chuyển tab: Thống kê");
        // loadCenterView("/com/bluemoon/app/views/ThongKeView.fxml");
    }

    // --- CHỨC NĂNG HỆ THỐNG (Đổi toàn bộ màn hình) ---

    // 5. Đăng xuất
    @FXML
    public void handleLogout(ActionEvent event) {
        System.out.println("Đang đăng xuất...");
        // Đăng xuất thì cần thay đổi cả cửa sổ (Scene) để về màn Login
        changeWholeScene(event, "/com/bluemoon/app/views/Login.fxml", "Hệ thống quản lý Blue Moon - Đăng nhập");
    }

    // --- CÁC HÀM TIỆN ÍCH (HELPER METHODS) ---

    /**
     * Hàm này dùng để chuyển đổi nội dung bên trong Dashboard (giữ Sidebar)
     */
    private void loadCenterView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainBorderPane.setCenter(view); // QUAN TRỌNG: Chỉ set lại phần giữa
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không tìm thấy file giao diện con: " + fxmlPath + "\nHãy kiểm tra lại đường dẫn thư mục!");
        }
    }

    /**
     * Hàm này dùng cho Đăng xuất: Thay thế toàn bộ cửa sổ ứng dụng
     */
    private void changeWholeScene(ActionEvent event, String fxmlPath, String title) {
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
            showError("Không tìm thấy file màn hình: " + fxmlPath);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi hệ thống");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}