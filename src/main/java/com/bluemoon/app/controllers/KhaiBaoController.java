package com.bluemoon.app.controllers;

import com.bluemoon.app.models.NhanKhauModel;
import com.bluemoon.app.services.TamTruTamVangService;
import com.bluemoon.app.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;

public class KhaiBaoController {

    @FXML private Label lblHoTen;
    @FXML private ComboBox<String> cbLoaiHinh;
    @FXML private TextField txtDiaChi; // Nơi tạm trú hoặc Nơi đến tạm vắng
    @FXML private DatePicker dpThoiGian;
    @FXML private TextArea txtNoiDung; // Lý do

    private NhanKhauModel currentNhanKhau;

    @FXML
    public void initialize() {
        cbLoaiHinh.getItems().addAll("Đăng ký Tạm trú", "Khai báo Tạm vắng");
        cbLoaiHinh.getSelectionModel().selectFirst();
    }

    public void setNhanKhau(NhanKhauModel nk) {
        this.currentNhanKhau = nk;
        lblHoTen.setText("Nhân khẩu: " + nk.getHoTen() + " - CCCD: " + nk.getCccd());
    }

    @FXML
    private void handleSave() {
        if (txtDiaChi.getText().isEmpty() || dpThoiGian.getValue() == null) {
            AlertUtils.showWarning("Vui lòng nhập địa chỉ và thời gian!");
            return;
        }

        int trangThai = cbLoaiHinh.getSelectionModel().getSelectedIndex(); // 0: Tạm trú, 1: Tạm vắng
        String diaChi = txtDiaChi.getText();
        Date thoiGian = Date.valueOf(dpThoiGian.getValue());
        String noiDung = txtNoiDung.getText();

        if (TamTruTamVangService.addTamTruTamVang(currentNhanKhau.getId(), trangThai, diaChi, thoiGian, noiDung)) {
            AlertUtils.showSuccess("Khai báo thành công!");
            closeWindow();
        } else {
            AlertUtils.showError("Lỗi hệ thống!");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) txtDiaChi.getScene().getWindow()).close();
    }
}