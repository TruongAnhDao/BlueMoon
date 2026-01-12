package com.bluemoon.app.controllers;

import com.bluemoon.app.services.ThongKeService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ThongKeController implements Initializable {

    @FXML private PieChart pieGioiTinh;
    @FXML private BarChart<String, Number> barDoTuoi;
    @FXML private BarChart<String, Number> barKhoanThu;
    @FXML private PieChart pieTamTruTamVang;

    @FXML private Label lblTotalNhanKhau;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadThongKeGioiTinh();
        loadThongKeDoTuoi();
        loadThongKeKhoanThu();
        loadThongKeTamTruTamVang();
    }

    // 1. Biểu đồ tròn Giới tính
    private void loadThongKeGioiTinh() {
        Map<String, Integer> data = ThongKeService.getNhanKhauByGioiTinh();
        pieGioiTinh.getData().clear();
        int total = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            pieGioiTinh.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            total += entry.getValue();
        }
        if (lblTotalNhanKhau != null) {
            lblTotalNhanKhau.setText("Tổng nhân khẩu: " + total);
        }
    }

    // 2. Biểu đồ cột Độ tuổi
    private void loadThongKeDoTuoi() {
        Map<String, Integer> data = ThongKeService.getNhanKhauByDoTuoi();
        barDoTuoi.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng người");

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barDoTuoi.getData().add(series);
    }

    // 3. Biểu đồ cột Doanh thu
    private void loadThongKeKhoanThu() {
        Map<String, Double> data = ThongKeService.getTongThuTungKhoan();
        barKhoanThu.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tổng tiền thu (VNĐ)");

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barKhoanThu.getData().add(series);
    }
    
    // 4. Biểu đồ tròn Tạm trú/Tạm vắng
    private void loadThongKeTamTruTamVang() {
        Map<String, Integer> data = ThongKeService.getTamTruTamVang();
        pieTamTruTamVang.getData().clear();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            pieTamTruTamVang.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }
}