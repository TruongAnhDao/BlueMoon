package com.bluemoon.app.controllers;

import com.bluemoon.app.models.ThuPhiModel;
import com.bluemoon.app.services.ThuPhiService;
import com.bluemoon.app.utils.AlertUtils;
import com.bluemoon.app.utils.FormatUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ThuPhiController implements Initializable {

    // --- LIÊN KẾT FXML (Khớp với fx:id trong ThuPhiView.fxml) ---
    @FXML private TextField searchField;
    @FXML private TableView<ThuPhiModel> historyTable;
    @FXML private TableColumn<ThuPhiModel, Integer> colId;
    @FXML private TableColumn<ThuPhiModel, Integer> colMaHoKhau;
    @FXML private TableColumn<ThuPhiModel, String> colTenKhoanThu;
    @FXML private TableColumn<ThuPhiModel, Double> colSoTien;
    @FXML private TableColumn<ThuPhiModel, java.util.Date> colNgayNop;
    @FXML private TableColumn<ThuPhiModel, String> colNguoiNop;
    @FXML private Label lblTotalRecords;

    private ObservableList<ThuPhiModel> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadData();
    }

    // 1. Cấu hình các cột cho TableView
    private void setupTableColumns() {
        // Ánh xạ thuộc tính từ ThuPhiModel vào cột
        colId.setCellValueFactory(new PropertyValueFactory<>("idNopTien"));
        colMaHoKhau.setCellValueFactory(new PropertyValueFactory<>("maHo"));
        colTenKhoanThu.setCellValueFactory(new PropertyValueFactory<>("tenKhoanThu"));
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("soTienNop"));
        colNgayNop.setCellValueFactory(new PropertyValueFactory<>("ngayNop"));
        colNguoiNop.setCellValueFactory(new PropertyValueFactory<>("nguoiNop"));

        // Định dạng cột Số tiền sang VNĐ bằng FormatUtils
        colSoTien.setCellFactory(column -> new TableCell<ThuPhiModel, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(FormatUtils.formatCurrency(item));
                }
            }
        });

        // Định dạng cột Ngày nộp sang dd/MM/yyyy
        colNgayNop.setCellFactory(column -> new TableCell<ThuPhiModel, java.util.Date>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(FormatUtils.formatDate(item));
                }
            }
        });
    }

    // 2. Tải dữ liệu từ database thông qua Service
    private void loadData() {
        List<ThuPhiModel> data = ThuPhiService.getAllPaymentHistory();
        masterData.setAll(data);
        historyTable.setItems(masterData);
        updateTotalCount();
    }

    // 3. Xử lý tìm kiếm theo mã hộ
    @FXML
    void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            List<ThuPhiModel> filteredData = ThuPhiService.searchByMaHo(keyword);
            historyTable.setItems(FXCollections.observableArrayList(filteredData));
            updateTotalCount();
        }
    }

    // 4. Xử lý làm mới dữ liệu
    @FXML
    void handleRefresh(ActionEvent event) {
        searchField.clear();
        loadData();
    }

    // 5. Xử lý xóa lịch sử thu phí
    @FXML
    void handleDeletePayment(ActionEvent event) {
        ThuPhiModel selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Vui lòng chọn một bản ghi để xóa!");
            return;
        }

        boolean confirm = AlertUtils.showConfirmation("Bạn có chắc chắn muốn xóa lịch sử nộp tiền này?");
        if (confirm) {
            boolean success = ThuPhiService.deletePayment(selected.getIdNopTien());
            if (success) {
                AlertUtils.showSuccess("Xóa thành công!");
                loadData();
            } else {
                AlertUtils.showError("Lỗi khi xóa dữ liệu!");
            }
        }
    }

    // 6. Mở màn hình ghi nhận khoản thu mới
    @FXML
    void handleAddNewPayment(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/bluemoon/views/AddThuPhiView.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Ghi nhận khoản thu mới");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait(); // Đợi đóng cửa sổ thêm mới xong
            loadData(); // Tải lại bảng để thấy dữ liệu mới
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Không thể mở form thêm mới!");
        }
    }

    private void updateTotalCount() {
        lblTotalRecords.setText("Tổng số bản ghi: " + historyTable.getItems().size());
    }
}