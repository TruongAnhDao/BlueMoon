package com.bluemoon.app.controllers;

import com.bluemoon.app.models.NhanKhauModel;
import com.bluemoon.app.services.NhanKhauService;
import com.bluemoon.app.utils.AlertUtils;
import com.bluemoon.app.utils.FormatUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class NhanKhauController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private TableView<NhanKhauModel> tableNhanKhau;
    @FXML private TableColumn<NhanKhauModel, String> colHoTen;
    @FXML private TableColumn<NhanKhauModel, Date> colNgaySinh;
    @FXML private TableColumn<NhanKhauModel, String> colGioiTinh;
    @FXML private TableColumn<NhanKhauModel, String> colCCCD;
    @FXML private TableColumn<NhanKhauModel, String> colNgheNghiep;
    @FXML private TableColumn<NhanKhauModel, String> colDanToc;

    private ObservableList<NhanKhauModel> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colNgheNghiep.setCellValueFactory(new PropertyValueFactory<>("ngheNghiep"));
        colDanToc.setCellValueFactory(new PropertyValueFactory<>("danToc"));

        // Format ngày sinh
        colNgaySinh.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(FormatUtils.formatDate(item));
            }
        });
    }

    private void loadData() {
        masterData.setAll(NhanKhauService.getAllNhanKhauFull());
        tableNhanKhau.setItems(masterData);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) loadData();
        else tableNhanKhau.setItems(FXCollections.observableArrayList(NhanKhauService.searchNhanKhau(keyword)));
    }

    @FXML
    private void handleAdd() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/bluemoon/views/AddNhanKhauView.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm nhân khẩu mới");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
            loadData(); // Load lại sau khi thêm
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi mở form thêm mới!");
        }
    }

    @FXML
    private void handleDelete() {
        NhanKhauModel selected = tableNhanKhau.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Vui lòng chọn nhân khẩu để xóa!");
            return;
        }
        if (AlertUtils.showConfirmation("Bạn có chắc muốn xóa: " + selected.getHoTen() + "?")) {
            if (NhanKhauService.deleteNhanKhau(selected.getId())) {
                AlertUtils.showSuccess("Xóa thành công!");
                loadData();
            } else {
                AlertUtils.showError("Không thể xóa (Có thể người này đang là Chủ hộ)!");
            }
        }
    }
}