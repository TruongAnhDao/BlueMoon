package com.bluemoon.app.controllers;

import com.bluemoon.app.models.NhanKhauModel;
import com.bluemoon.app.utils.FormatUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DetailNhanKhauController {

    @FXML private Label lblHoTen, lblNgaySinh, lblGioiTinh, lblDanToc, lblTonGiao;
    @FXML private Label lblCCCD, lblNgayCap, lblNoiCap, lblNgheNghiep, lblGhiChu;

    private NhanKhauModel currentNhanKhau;

    public void setNhanKhau(NhanKhauModel nk) {
        this.currentNhanKhau = nk;
        
        // Hiển thị thông tin lên các Label
        lblHoTen.setText(nk.getHoTen());
        lblNgaySinh.setText(FormatUtils.formatDate(nk.getNgaySinh()));
        lblGioiTinh.setText(nk.getGioiTinh());
        lblDanToc.setText(nk.getDanToc());
        lblTonGiao.setText(nk.getTonGiao());
        lblCCCD.setText(nk.getCccd());
        lblNgayCap.setText(FormatUtils.formatDate(nk.getNgayCap()));
        lblNoiCap.setText(nk.getNoiCap());
        lblNgheNghiep.setText(nk.getNgheNghiep());
        lblGhiChu.setText(nk.getGhiChu());
    }

    @FXML
    private void handleKhaiBao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bluemoon/views/KhaiBaoView.fxml"));
            Parent root = loader.load();
            
            KhaiBaoController controller = loader.getController();
            controller.setNhanKhau(currentNhanKhau); // Truyền người này sang màn hình khai báo
            
            Stage stage = new Stage();
            stage.setTitle("Khai báo cho: " + currentNhanKhau.getHoTen());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) lblHoTen.getScene().getWindow()).close();
    }
}