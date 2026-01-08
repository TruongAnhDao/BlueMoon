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

    // 1. LIÊN KẾT VỚI FXML
    // Bạn bắt buộc phải thêm fx:id="mainBorderPane" vào thẻ BorderPane trong file Dashboard.fxml
    @FXML
    private BorderPane mainBorderPane; 

    private Parent homeView; // Biến để lưu giữ màn hình "Chào mừng" ban đầu

    // 2. KHỞI TẠO (Chạy ngay khi giao diện hiện lên)
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Lưu lại giao diện chào mừng mặc định để dùng cho nút "Trang chủ"
        if (mainBorderPane != null) {
            homeView = (Parent) mainBorderPane.getCenter();
        }
    }

    // --- NHÓM 1: CÁC NÚT MENU (Giữ nguyên Sidebar, chỉ thay đổi phần giữa) ---

    @FXML
    public void handleGoToHome(ActionEvent event) {
        // Quay lại màn hình chào mừng ban đầu
        if (homeView != null) {
            mainBorderPane.setCenter(homeView);
        }
    }

    @FXML
    public void handleGoToHoKhau(ActionEvent event) {
        System.out.println("Chuyển tab: Quản lý hộ khẩu");
        // Gọi hàm load phần giữa
        // LƯU Ý: Kiểm tra kỹ đường dẫn file FXML của bạn
        loadCenterView("/com/bluemoon/views/HoKhauView.fxml"); 
    }

    @FXML
    public void handleGoToKhoanThu(ActionEvent event) {
        System.out.println("Chuyển tab: Quản lý khoản thu");
        loadCenterView("/com/bluemoon/views/KhoanThuView.fxml");
    }

    @FXML
    public void handleGoToThuPhi(ActionEvent event) {
        System.out.println("Chuyển tab: Thu Phí");
        loadCenterView("/com/bluemoon/views/ThuphiView.fxml");
    }


    // --- NHÓM 2: CÁC NÚT HỆ THỐNG (Thay đổi toàn bộ cửa sổ) ---

    @FXML
    public void handleLogout(ActionEvent event) {
        System.out.println("Đang đăng xuất...");
        // Đăng xuất thì dùng changeScene để đổi sang màn hình Login
        changeScene(event, "/com/bluemoon/views/Login.fxml", "Hệ thống quản lý Blue Moon");
    }


    // --- CÁC HÀM XỬ LÝ (HELPER METHODS) ---

    /**
     * HÀM 1: loadCenterView
     * Dùng để nhét file FXML con vào vị trí giữa (Center) của Dashboard
     */
    private void loadCenterView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainBorderPane.setCenter(view); // Chỉ thay đổi phần giữa
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi tải giao diện", "Không tìm thấy file: " + fxmlPath + "\nHãy kiểm tra lại đường dẫn!");
        }
    }

    /**
     * HÀM 2: changeScene
     * Dùng để chuyển đổi toàn bộ cửa sổ (VD: Đăng nhập -> Dashboard -> Đăng xuất)
     */
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
            showAlert("Lỗi hệ thống", "Không tìm thấy file màn hình: " + fxmlPath);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        
        alert.setContentText(content);
        alert.show();
    }
}