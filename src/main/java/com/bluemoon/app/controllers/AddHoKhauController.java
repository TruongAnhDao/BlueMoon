package com.bluemoon.app.controllers;

import com.bluemoon.app.models.HoKhauModel;
import com.bluemoon.app.models.NhanKhauModel;
import com.bluemoon.app.services.HoKhauService;
import com.bluemoon.app.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddHoKhauController {

    @FXML private TextField txtMaHo;
    @FXML private TextField txtDiaChi;
    @FXML private ComboBox<NhanKhauModel> cbChuHo;

    @FXML
    public void initialize() {
        // Tải danh sách nhân khẩu vào ComboBox để chọn
        cbChuHo.getItems().addAll(HoKhauService.getAllNhanKhau());
    }

    @FXML
    private void handleSave() {
        try {
            // Validate dữ liệu
            if (txtMaHo.getText().isEmpty() || txtDiaChi.getText().isEmpty() || cbChuHo.getValue() == null) {
                AlertUtils.showWarning("Vui lòng nhập đủ thông tin và chọn chủ hộ!");
                return;
            }

            int maHo = Integer.parseInt(txtMaHo.getText());
            String diaChi = txtDiaChi.getText();
            NhanKhauModel chuHo = cbChuHo.getValue();

            HoKhauModel newHoKhau = new HoKhauModel();
            newHoKhau.setMaHoKhau(maHo);
            newHoKhau.setDiaChi(diaChi);

            // Gọi Service
            if (HoKhauService.addHoKhau(newHoKhau, chuHo.getId())) {
                AlertUtils.showSuccess("Thêm hộ khẩu mới thành công!");
                closeWindow();
            } else {
                AlertUtils.showError("Lỗi khi thêm! Có thể mã hộ đã tồn tại.");
            }

        } catch (NumberFormatException e) {
            AlertUtils.showError("Mã hộ khẩu phải là số!");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtMaHo.getScene().getWindow();
        stage.close();
    }
}