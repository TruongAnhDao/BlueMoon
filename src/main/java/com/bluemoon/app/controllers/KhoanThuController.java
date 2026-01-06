package com.bluemoon.app.controllers;

import com.bluemoon.app.models.KhoanThuModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class KhoanThuController implements Initializable {

    // --- LIÊN KẾT FXML ---
    @FXML private TextField searchTextField;
    @FXML private Button btnSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnExcel;
    @FXML private Label lblTotalRecord;

    // --- BẢNG VÀ CỘT (Cập nhật theo Model mới) ---
    @FXML private TableView<KhoanThuModel> feeTable;
    
    // Cột Mã: Model dùng int -> TableColumn phải là Integer
    @FXML private TableColumn<KhoanThuModel, Integer> colMaKhoanThu;
    
    // Cột Tên: String
    @FXML private TableColumn<KhoanThuModel, String> colTenKhoanThu;
    
    // Cột Đơn giá: Model tên là "soTien" (double) -> TableColumn Double
    @FXML private TableColumn<KhoanThuModel, Double> colDonGia;
    
    // Cột Loại phí: Model dùng int -> TableColumn Integer
    @FXML private TableColumn<KhoanThuModel, Integer> colLoaiPhi;
    
    // Cột Thao tác: Void (chứa nút bấm)
    @FXML private TableColumn<KhoanThuModel, Void> colThaoTac;

    // --- DỮ LIỆU ---
    private final ObservableList<KhoanThuModel> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadDummyData();
        setupSearch();
        setupButtonActions();
    }

    // 1. CẤU HÌNH CỘT CHO BẢNG
    private void setupTableColumns() {
        // Tên PropertyValueFactory PHẢI KHỚP Y HỆT tên biến trong KhoanThuModel
        colMaKhoanThu.setCellValueFactory(new PropertyValueFactory<>("maKhoanThu"));
        colTenKhoanThu.setCellValueFactory(new PropertyValueFactory<>("tenKhoanThu"));
        
        // Model là "soTien" nhưng hiển thị lên cột "Đơn giá"
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("soTien"));
        
        // Model là "loaiKhoanThu"
        colLoaiPhi.setCellValueFactory(new PropertyValueFactory<>("loaiKhoanThu"));

        // --- FORMAT HIỂN THỊ ---

        // Format tiền tệ (VD: 120,000 VNĐ)
        colDonGia.setCellFactory(column -> new TableCell<KhoanThuModel, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VNĐ", item));
                }
            }
        });

        // Format Loại phí (VD: int 1 -> "Bắt buộc", int 0 -> "Tự nguyện")
        colLoaiPhi.setCellFactory(column -> new TableCell<KhoanThuModel, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Giả sử quy ước: 1 là Bắt buộc, 0 là Tự nguyện
                    if (item == 1) {
                        setText("Bắt buộc");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Màu đỏ
                    } else {
                        setText("Tự nguyện");
                        setStyle("-fx-text-fill: #2ecc71;"); // Màu xanh lá
                    }
                }
            }
        });

        // Tạo nút Sửa/Xóa
        addButtonToTable();
    }

    // 2. TẠO DỮ LIỆU GIẢ (Dùng constructor mới của Model)
    private void loadDummyData() {
        // Constructor: (int ma, String ten, double tien, int loai)
        masterData.add(new KhoanThuModel(101, "Phí quản lý chung cư", 7000.0, 1));
        masterData.add(new KhoanThuModel(102, "Phí gửi xe máy", 120000.0, 0));
        masterData.add(new KhoanThuModel(103, "Phí gửi ô tô", 1200000.0, 0));
        masterData.add(new KhoanThuModel(104, "Tiền nước sạch", 15000.0, 1));
        masterData.add(new KhoanThuModel(105, "Quỹ vì người nghèo", 50000.0, 0));

        // Đưa dữ liệu vào bảng
        feeTable.setItems(masterData);
        updateTotalCount();
    }

    // 3. XỬ LÝ NÚT SỬA / XÓA
    private void addButtonToTable() {
        Callback<TableColumn<KhoanThuModel, Void>, TableCell<KhoanThuModel, Void>> cellFactory = new Callback<TableColumn<KhoanThuModel, Void>, TableCell<KhoanThuModel, Void>>() {
            @Override
            public TableCell<KhoanThuModel, Void> call(final TableColumn<KhoanThuModel, Void> param) {
                return new TableCell<KhoanThuModel, Void>() {
                    private final Button btnEdit = new Button("Sửa");
                    private final Button btnDelete = new Button("Xóa");

                    {
                        btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");
                        
                        btnEdit.setOnAction((event) -> {
                            KhoanThuModel data = getTableView().getItems().get(getIndex());
                            System.out.println("Sửa khoản thu: " + data.getTenKhoanThu());
                        });

                        btnDelete.setOnAction((event) -> {
                            KhoanThuModel data = getTableView().getItems().get(getIndex());
                            handleDelete(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(5, btnEdit, btnDelete);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        };
        colThaoTac.setCellFactory(cellFactory);
    }

    // 4. CÁC NÚT ACTION
    private void setupButtonActions() {
        btnAdd.setOnAction(e -> System.out.println("Click Thêm mới"));
        btnExcel.setOnAction(e -> System.out.println("Click Xuất Excel"));
        btnSearch.setOnAction(e -> filterData(searchTextField.getText()));
    }
    
    // 5. TÌM KIẾM
    private void setupSearch() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData(newValue);
        });
    }

    private void filterData(String keyword) {
        FilteredList<KhoanThuModel> filteredData = new FilteredList<>(masterData, p -> true);
        
        filteredData.setPredicate(item -> {
            if (keyword == null || keyword.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = keyword.toLowerCase();
            
            // Tìm theo Tên
            if (item.getTenKhoanThu().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } 
            // Tìm theo Mã (phải đổi int sang String để so sánh)
            else if (String.valueOf(item.getMaKhoanThu()).contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
        
        feeTable.setItems(filteredData);
        updateTotalCount();
    }

    private void handleDelete(KhoanThuModel item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có muốn xóa: " + item.getTenKhoanThu() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            masterData.remove(item);
            updateTotalCount();
        }
    }

    private void updateTotalCount() {
        lblTotalRecord.setText(String.valueOf(feeTable.getItems().size()));
    }
}