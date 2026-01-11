package com.bluemoon.app.controllers;

import com.bluemoon.app.models.NhanKhauModel;
import com.bluemoon.app.services.NhanKhauService;
import com.bluemoon.app.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.sql.Date;

public class AddNhanKhauController {

    @FXML private TextField txtHoTen, txtCCCD, txtDanToc, txtTonGiao, txtNoiCap, txtNgheNghiep;
    @FXML private DatePicker dpNgaySinh, dpNgayCap;
    @FXML private ComboBox<String> cbGioiTinh;
    @FXML private TextArea txtGhiChu;

    @FXML
    public void initialize() {
        cbGioiTinh.getItems().addAll("Nam", "Nữ", "Khác");
    }

    @FXML
    private void handleSave() {
        // 1. Validate
        if (txtHoTen.getText().isEmpty() || dpNgaySinh.getValue() == null || cbGioiTinh.getValue() == null) {
            AlertUtils.showWarning("Vui lòng nhập Họ tên, Ngày sinh và Giới tính!");
            return;
        }

        try {
            // 2. Map data to Model
            NhanKhauModel nk = new NhanKhauModel();
            nk.setHoTen(txtHoTen.getText());
            nk.setNgaySinh(Date.valueOf(dpNgaySinh.getValue())); // Convert LocalDate -> SQL Date
            nk.setGioiTinh(cbGioiTinh.getValue());
            nk.setDanToc(txtDanToc.getText());
            nk.setTonGiao(txtTonGiao.getText());
            nk.setCccd(txtCCCD.getText());
            
            if (dpNgayCap.getValue() != null) 
                nk.setNgayCap(Date.valueOf(dpNgayCap.getValue()));
                
            nk.setNoiCap(txtNoiCap.getText());
            nk.setNgheNghiep(txtNgheNghiep.getText());
            nk.setGhiChu(txtGhiChu.getText());

            // 3. Save to DB
            if (NhanKhauService.addNhanKhau(nk)) {
                AlertUtils.showSuccess("Thêm nhân khẩu thành công!");
                handleCancel();
            } else {
                AlertUtils.showError("Lỗi hệ thống!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Dữ liệu không hợp lệ!");
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) txtHoTen.getScene().getWindow()).close();
    }
}