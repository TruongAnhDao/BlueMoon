package com.bluemoon.app.controllers;

import com.bluemoon.app.models.HoKhauModel;
import com.bluemoon.app.services.HoKhauService;
import com.bluemoon.app.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class HoKhauController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private TableView<HoKhauModel> tableHoKhau;
    @FXML private TableColumn<HoKhauModel, Integer> colMaHo;
    @FXML private TableColumn<HoKhauModel, String> colChuHo;
    @FXML private TableColumn<HoKhauModel, String> colDiaChi; // Trong FXML cũ là Diện tích, tôi sửa thành Địa chỉ cho đúng nghiệp vụ
    @FXML private TableColumn<HoKhauModel, Integer> colThanhVien; // Thay cho trạng thái
    @FXML private TableColumn<HoKhauModel, Void> colThaoTac;

    private ObservableList<HoKhauModel> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colMaHo.setCellValueFactory(new PropertyValueFactory<>("maHoKhau"));
        colChuHo.setCellValueFactory(new PropertyValueFactory<>("tenChuHo"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        colThanhVien.setCellValueFactory(new PropertyValueFactory<>("soThanhVien"));

        // Thêm nút Xem/Sửa/Xóa
        colThaoTac.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");

            {
                btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");
                
                btnEdit.setOnAction(event -> {
                    HoKhauModel selectedData = getTableView().getItems().get(getIndex());
                        try {
                            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/bluemoon/views/EditHoKhauView.fxml"));
                            javafx.scene.Parent root = loader.load();
        
                            // Lấy controller của màn hình Edit để truyền dữ liệu vào
                            EditHoKhauController editController = loader.getController();
                            editController.setHoKhauData(selectedData); // Truyền dữ liệu dòng đang chọn sang
        
                            javafx.stage.Stage stage = new javafx.stage.Stage();
                            stage.setTitle("Sửa thông tin hộ khẩu");
                            stage.setScene(new javafx.scene.Scene(root));
                            stage.showAndWait(); // Chờ sửa xong
        
                            loadData(); // Tải lại bảng để cập nhật thông tin mới
        
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertUtils.showError("Không thể mở form sửa: " + e.getMessage());
                        }
                });

                btnDelete.setOnAction(event -> {
                    HoKhauModel data = getTableView().getItems().get(getIndex());
                    if (AlertUtils.showConfirmation("Bạn chắc chắn muốn xóa hộ này?")) {
                        if (HoKhauService.deleteHoKhau(data.getMaHoKhau())) {
                            AlertUtils.showSuccess("Xóa thành công!");
                            loadData();
                        } else {
                            AlertUtils.showError("Không thể xóa (Có thể do ràng buộc dữ liệu nhân khẩu/thu phí)!");
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new HBox(5, btnEdit, btnDelete));
            }
        });
    }

    private void loadData() {
        masterData.setAll(HoKhauService.getAllHoKhau());
        tableHoKhau.setItems(masterData);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) loadData();
        else {
            tableHoKhau.setItems(FXCollections.observableArrayList(HoKhauService.searchHoKhau(keyword)));
        }
    }

    @FXML
    private void handleAdd() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/bluemoon/views/AddHoKhauView.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Thêm hộ khẩu");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait(); // Chờ đóng cửa sổ
            
            loadData(); // Tải lại bảng sau khi thêm xong
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi mở form thêm mới: " + e.getMessage());
        }
    }
}