package com.bluemoon.app.controllers;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import com.bluemoon.app.models.KhoanThuModel;
import com.bluemoon.app.services.KhoanThuService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class KhoanThuController implements Initializable {

    // --- Khai báo các biến giao diện (FXML) ---
    // Đảm bảo fx:id trong SceneBuilder trùng khớp với các tên biến này
    @FXML private TextField searchTextField;
    @FXML private Button btnSearch;
    @FXML private Button btnAdd;
    @FXML private TableView<KhoanThuModel> feeTable;
    @FXML private TableColumn<KhoanThuModel, Integer> colMaKhoanThu;
    @FXML private TableColumn<KhoanThuModel, String> colTenKhoanThu;
    @FXML private TableColumn<KhoanThuModel, Double> colDonGia;
    @FXML private TableColumn<KhoanThuModel, String> colLoaiPhi;
    @FXML private TableColumn<KhoanThuModel, Void> colThaoTac; // Cột chứa nút Xóa

    // --- Khai báo Service và List dữ liệu ---
    private final KhoanThuService khoanThuService = new KhoanThuService();
    private ObservableList<KhoanThuModel> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();       // Cấu hình cột
        loadDataFromDB();   // Tải dữ liệu ban đầu
        setupActions();     // Gán sự kiện cho nút bấm
    }

    // 1. Cấu hình bảng
    private void setupTable() {
        colMaKhoanThu.setCellValueFactory(new PropertyValueFactory<>("maKhoanThu"));
        colTenKhoanThu.setCellValueFactory(new PropertyValueFactory<>("tenKhoanThu"));
        
        // Format tiền tệ (VNĐ)
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("soTien"));
        colDonGia.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(item));
                }
            }
        });

        // Chuyển đổi hiển thị: 0 -> Bắt buộc, 1 -> Tự nguyện
        colLoaiPhi.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getLoaiKhoanThu() == 0 ? "Bắt buộc" : "Tự nguyện"
            )
        );
        
        // Thêm nút Xóa vào từng dòng
        addDeleteButtonToTable();
    }

    // 2. Tải dữ liệu từ Service
    private void loadDataFromDB() {
        masterData.clear();
        masterData.addAll(khoanThuService.getAllKhoanThu());
        feeTable.setItems(masterData);
    }

    // 3. Gán sự kiện cho các nút
    private void setupActions() {
        // Sự kiện Tìm kiếm
        btnSearch.setOnAction(event -> {
            String keyword = searchTextField.getText().trim();
            masterData.clear();
            if (keyword.isEmpty()) {
                masterData.addAll(khoanThuService.getAllKhoanThu());
            } else {
                masterData.addAll(khoanThuService.searchKhoanThu(keyword));
            }
            feeTable.setItems(masterData);
        });

        // Sự kiện Thêm mới
        btnAdd.setOnAction(event -> showAddDialog());
    }

    // 4. Hiển thị Dialog Thêm mới
    private void showAddDialog() {
        Dialog<KhoanThuModel> dialog = new Dialog<>();
        dialog.setTitle("Thêm khoản thu");
        dialog.setHeaderText("Nhập thông tin khoản thu mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfTen = new TextField();
        tfTen.setPromptText("Tên khoản thu");
        TextField tfTien = new TextField();
        tfTien.setPromptText("Số tiền (VNĐ)");

        ComboBox<String> cbLoai = new ComboBox<>();
        cbLoai.getItems().addAll("Bắt buộc", "Tự nguyện");
        cbLoai.getSelectionModel().selectFirst();

        grid.add(new Label("Tên khoản thu:"), 0, 0);
        grid.add(tfTen, 1, 0);
        grid.add(new Label("Số tiền:"), 0, 1);
        grid.add(tfTien, 1, 1);
        grid.add(new Label("Loại phí:"), 0, 2);
        grid.add(cbLoai, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String ten = tfTen.getText();
                    double tien = Double.parseDouble(tfTien.getText());
                    int loai = cbLoai.getSelectionModel().getSelectedIndex();
                    
                    if (!ten.isEmpty() && tien >= 0) {
                        return new KhoanThuModel(0, ten, tien, loai);
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<KhoanThuModel> result = dialog.showAndWait();
        result.ifPresent(khoanThu -> {
            if (khoanThuService.addKhoanThu(khoanThu)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khoản thu mới!");
                loadDataFromDB();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Thêm thất bại (Vui lòng kiểm tra lại dữ liệu).");
            }
        });
    }

    // 5. Tạo nút Xóa trong bảng
    private void addDeleteButtonToTable() {
        Callback<TableColumn<KhoanThuModel, Void>, TableCell<KhoanThuModel, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Xóa");
            {
                btn.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red; -fx-border-color: red;");
                btn.setOnAction(event -> {
                    KhoanThuModel data = getTableView().getItems().get(getIndex());
                    
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận xóa");
                    alert.setHeaderText("Bạn có chắc chắn muốn xóa: " + data.getTenKhoanThu() + "?");
                    
                    Optional<ButtonType> option = alert.showAndWait();
                    if (option.isPresent() && option.get() == ButtonType.OK) {
                        if (khoanThuService.deleteKhoanThu(data.getMaKhoanThu())) {
                            loadDataFromDB();
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa khoản thu.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa (Có thể khoản thu này đang có dữ liệu nộp tiền).");
                        }
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
        colThaoTac.setCellFactory(cellFactory);
    }

    // 6. Hàm tiện ích hiển thị thông báo (Thay thế cho AlertUtils)
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}