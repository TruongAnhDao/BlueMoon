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
        // Tạo một Dialog nhỏ để chọn người thêm vào
        Dialog<NhanKhauModel> dialog = new Dialog<>();
        dialog.setTitle("Thêm thành viên");
        dialog.setHeaderText("Chọn nhân khẩu từ hệ thống");

        // Nút bấm
        ButtonType loginButtonType = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Giao diện bên trong Dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<NhanKhauModel> cbAllPeople = new ComboBox<>();
        cbAllPeople.setPromptText("Tìm chọn nhân khẩu...");
        cbAllPeople.setPrefWidth(300);
        
        // Tải TOÀN BỘ nhân khẩu vào đây để chọn (Lưu ý: Thực tế nên lọc những người chưa có hộ khẩu)
        cbAllPeople.getItems().addAll(HoKhauService.getAllNhanKhau()); 

        TextField txtQuanHe = new TextField();
        txtQuanHe.setPromptText("Quan hệ với chủ hộ (VD: Con, Cháu...)");

        grid.add(new Label("Nhân khẩu:"), 0, 0);
        grid.add(cbAllPeople, 1, 0);
        grid.add(new Label("Quan hệ:"), 0, 1);
        grid.add(txtQuanHe, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Logic trả về kết quả
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return cbAllPeople.getValue();
            }
            return null;
        });

        Optional<NhanKhauModel> result = dialog.showAndWait();

        result.ifPresent(person -> {
            String quanHe = txtQuanHe.getText().isEmpty() ? "Thành viên" : txtQuanHe.getText();
            if (HoKhauService.addMember(person.getId(), currentMaHo, quanHe)) {
                AlertUtils.showSuccess("Thêm thành viên thành công!");
                loadMembersData();
            } else {
                AlertUtils.showError("Thất bại! (Người này có thể đã có trong hộ này).");
            }
        });
    }

    @FXML
    private void handleClose() {
        ((Stage) txtMaHo.getScene().getWindow()).close();
    }
}