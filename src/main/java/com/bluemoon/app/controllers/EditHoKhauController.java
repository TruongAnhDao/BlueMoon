package com.bluemoon.app.controllers;

import java.util.List;

import com.bluemoon.app.models.HoKhauModel;
import com.bluemoon.app.models.NhanKhauModel;
import com.bluemoon.app.services.HoKhauService;
import com.bluemoon.app.utils.AlertUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.bluemoon.app.utils.FormatUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;

public class EditHoKhauController {

    // --- PHẦN TRÊN: THÔNG TIN CHUNG ---
    @FXML private TextField txtMaHo;
    @FXML private TextField txtDiaChi;
    @FXML private ComboBox<NhanKhauModel> cbChuHo;

    // --- PHẦN DƯỚI: BẢNG THÀNH VIÊN ---
    @FXML private TableView<NhanKhauModel> tableMembers;
    @FXML private TableColumn<NhanKhauModel, String> colTenTV;
    @FXML private TableColumn<NhanKhauModel, java.util.Date> colNgaySinhTV;
    @FXML private TableColumn<NhanKhauModel, String> colCccdTV;
    @FXML private TableColumn<NhanKhauModel, String> colQuanHeTV; 

    private int currentMaHo;
    private ObservableList<NhanKhauModel> memberList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        colTenTV.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colNgaySinhTV.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colCccdTV.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colQuanHeTV.setCellValueFactory(new PropertyValueFactory<>("quanHeVoiChuHo"));
        
        // Format ngày sinh
        colNgaySinhTV.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : FormatUtils.formatDate(item));
            }
        });
    }

    // Hàm nhận dữ liệu từ màn hình chính truyền sang
    public void setHoKhauData(HoKhauModel hoKhau) {
        this.currentMaHo = hoKhau.getMaHoKhau();
        
        // 1. Fill thông tin chung
        txtMaHo.setText(String.valueOf(currentMaHo));
        txtDiaChi.setText(hoKhau.getDiaChi());

        // 2. Load danh sách thành viên vào bảng và ComboBox chủ hộ
        loadMembersData();
    }

    private void loadMembersData() {
        memberList.clear();
        memberList.addAll(HoKhauService.getMembersOfHoKhau(currentMaHo));
        
        // Cập nhật bảng
        tableMembers.setItems(memberList);
        
        // Cập nhật ComboBox Chủ hộ (Chỉ những người trong nhà mới được làm chủ hộ)
        cbChuHo.setItems(memberList);
        
        // Chọn đúng chủ hộ hiện tại
        int currentChuHoId = HoKhauService.getChuHoIdByMaHo(currentMaHo);
        for (NhanKhauModel nk : cbChuHo.getItems()) {
            if (nk.getId() == currentChuHoId) {
                cbChuHo.getSelectionModel().select(nk);
                break;
            }
        }
    }

    // --- XỬ LÝ LƯU THÔNG TIN CHUNG ---
    @FXML
    private void handleSaveInfo() {
        try {
            if (txtDiaChi.getText().isEmpty() || cbChuHo.getValue() == null) {
                AlertUtils.showWarning("Vui lòng nhập địa chỉ và chọn chủ hộ!");
                return;
            }

            String diaChiMoi = txtDiaChi.getText();
            int idChuHoMoi = cbChuHo.getValue().getId();

            if (HoKhauService.updateHoKhau(currentMaHo, diaChiMoi, idChuHoMoi)) {
                AlertUtils.showSuccess("Cập nhật thông tin chung thành công!");
                loadMembersData(); // Refresh lại dữ liệu
            } else {
                AlertUtils.showError("Lỗi khi cập nhật!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- XỬ LÝ XÓA THÀNH VIÊN ---
    @FXML
    private void handleRemoveMember() {
        NhanKhauModel selected = tableMembers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Vui lòng chọn thành viên để xóa!");
            return;
        }

        boolean confirm = AlertUtils.showConfirmation("Bạn chắc chắn muốn xóa " + selected.getHoTen() + " khỏi hộ này?");
        if (confirm) {
            if (HoKhauService.removeMember(selected.getId(), currentMaHo)) {
                AlertUtils.showSuccess("Đã xóa thành viên!");
                loadMembersData(); // Tải lại bảng
            } else {
                AlertUtils.showError("Không thể xóa! (Có thể người này đang là Chủ hộ. Hãy đổi chủ hộ trước).");
            }
        }
    }

    // --- XỬ LÝ THÊM THÀNH VIÊN (POPUP ĐƠN GIẢN) ---
    @FXML
    private void handleAddMember() {
        // 1. Tạo Dialog tùy chỉnh
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Thêm thành viên vào hộ");
        dialog.setHeaderText("Tìm kiếm và chọn nhân khẩu");

        // 2. Các nút bấm (Thêm / Hủy)
        ButtonType addButtonType = new ButtonType("Thêm vào hộ", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // 3. Tạo giao diện bên trong Dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(500); // Mở rộng chiều ngang cho dễ nhìn

        // Ô nhập từ khóa tìm kiếm
        TextField txtSearchName = new TextField();
        txtSearchName.setPromptText("Nhập tên hoặc CCCD để tìm...");
        
        // Bảng hiển thị kết quả tìm kiếm
        TableView<NhanKhauModel> tableResult = new TableView<>();
        tableResult.setPrefHeight(200);
        
        TableColumn<NhanKhauModel, String> colName = new TableColumn<>("Họ tên");
        colName.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colName.setPrefWidth(180);
        
        TableColumn<NhanKhauModel, java.util.Date> colDob = new TableColumn<>("Ngày sinh");
        colDob.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colDob.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : FormatUtils.formatDate(item));
            }
        });
        
        TableColumn<NhanKhauModel, String> colCccd = new TableColumn<>("CCCD");
        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));

        tableResult.getColumns().addAll(colName, colDob, colCccd);
        tableResult.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Ô nhập quan hệ
        TextField txtQuanHe = new TextField();
        txtQuanHe.setPromptText("Quan hệ với chủ hộ (VD: Con, Vợ...)");

        // Layout
        grid.add(new Label("Tìm kiếm:"), 0, 0);
        grid.add(txtSearchName, 1, 0);
        grid.add(tableResult, 0, 1, 2, 1); // Bảng chiếm 2 cột
        grid.add(new Label("Quan hệ:"), 0, 2);
        grid.add(txtQuanHe, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // 4. Logic tìm kiếm (Gõ đến đâu tìm đến đó)
        txtSearchName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                tableResult.getItems().clear();
            } else {
                // Gọi Service tìm kiếm (cần đảm bảo NhanKhauService.searchNhanKhau đã hoạt động tốt)
                List<NhanKhauModel> results = com.bluemoon.app.services.NhanKhauService.searchNhanKhau(newValue.trim());
                tableResult.getItems().setAll(results);
            }
        });

        // Vô hiệu hóa nút "Thêm" nếu chưa chọn người
        javafx.scene.Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Khi chọn 1 dòng thì mới sáng nút Thêm
        tableResult.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            addButton.setDisable(newSelection == null);
        });

        // 5. Xử lý kết quả trả về
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                NhanKhauModel selectedPerson = tableResult.getSelectionModel().getSelectedItem();
                String quanHe = txtQuanHe.getText().trim();
                if (quanHe.isEmpty()) quanHe = "Thành viên";

                if (selectedPerson != null) {
                    // Gọi Service thêm vào DB
                    if (HoKhauService.addMember(selectedPerson.getId(), currentMaHo, quanHe)) {
                        return true; // Thành công
                    } else {
                        AlertUtils.showError("Người này đã có trong hộ hoặc lỗi hệ thống!");
                    }
                }
            }
            return false;
        });

        // Hiển thị Dialog và xử lý sau khi đóng
        Optional<Boolean> result = dialog.showAndWait();
        result.ifPresent(success -> {
            if (success) {
                AlertUtils.showSuccess("Thêm thành viên thành công!");
                loadMembersData(); // Tải lại bảng danh sách thành viên
            }
        });
    }

    @FXML
    private void handleClose() {
        ((Stage) txtMaHo.getScene().getWindow()).close();
    }
}