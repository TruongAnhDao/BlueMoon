package com.bluemoon.app.controllers;

import com.bluemoon.app.models.KhoanThuModel;
import com.bluemoon.app.models.ThuPhiModel;
import com.bluemoon.app.services.ThuPhiService;
import com.bluemoon.app.services.MysqlConnection;
import com.bluemoon.app.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.util.Date;

public class AddThuPhiController {
    @FXML private TextField txtMaHo, txtSoTien, txtNguoiNop;
    @FXML private ComboBox<KhoanThuModel> cbKhoanThu;

    @FXML
    public void initialize() {
        loadKhoanThu(); // Tải danh sách các loại phí vào ComboBox
    }

    private void loadKhoanThu() {
        try {
            String sql = "SELECT * FROM khoan_thu";
            ResultSet rs = MysqlConnection.executeQuery(sql);
            while (rs.next()) {
                cbKhoanThu.getItems().add(new KhoanThuModel(
                    rs.getInt("maKhoanThu"), 
                    rs.getString("tenKhoanThu"), 
                    rs.getDouble("soTien"), 
                    rs.getInt("loaiKhoanThu")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void handleSave() {
        try {
            // 1. Lấy dữ liệu từ Form
            int maHo = Integer.parseInt(txtMaHo.getText());
            KhoanThuModel selectedKT = cbKhoanThu.getSelectionModel().getSelectedItem();
            double soTien = Double.parseDouble(txtSoTien.getText());
            String nguoiNop = txtNguoiNop.getText();

            if (selectedKT == null || nguoiNop.isEmpty()) {
                AlertUtils.showWarning("Vui lòng nhập đủ thông tin!");
                return;
            }

            // 2. Tạo đối tượng Model
            ThuPhiModel payment = new ThuPhiModel();
            payment.setMaHo(maHo);
            payment.setMaKhoanThu(selectedKT.getMaKhoanThu());
            payment.setSoTienNop(soTien);
            payment.setNgayNop(new Date());
            payment.setNguoiNop(nguoiNop);

            // 3. Gọi Service để lưu vào DB
            if (ThuPhiService.addPayment(payment)) {
                AlertUtils.showSuccess("Ghi nhận nộp tiền thành công!");
                handleCancel(); // Đóng cửa sổ
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("Mã hộ và số tiền phải là số!");
        }
    }

    @FXML
    void handleCancel() {
        ((Stage) txtMaHo.getScene().getWindow()).close();
    }
}