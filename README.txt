1. Chuẩn bị môi trường
- Cài JDK 17+
- Cài MySQL Server
 
    *** Cách cài ***
    Link tải: https://dev.mysql.com/downloads/installer/ (Nhớ chọn bản dung lượng lớn)
    Sau khi mở file .msi thực hiện các bước
    1. Choosing a Setup Type: Chọn Custom 

    2. Select Products: Chọn 2 món sau đây và nhấn mũi tên sang phải:

    - MySQL Server 8.0.x 

    - MySQL Workbench 8.0.x 

    3. Installation: Nhấn Execute để máy tự cài.

    4. Type and Networking: Giữ nguyên mặc định (Port 3306).

    5. Authentication Method: Chọn Use Strong Password Encryption (Mặc định).

    6. Accounts and Roles (QUAN TRỌNG): * Đặt mật khẩu cho tài khoản root.

    ( Đặt là T06012005a@ để cả nhóm dùng chung )

    7. Apply Configuration: Nhấn Execute đến khi tất cả các dấu tích xanh hiện lên.

    ***----***

- Export file sql trên mes vào MySQL Workbench để có database chạy test
- Tìm và tải các Extension cần thiết: Extension Pack for Java, Gradle for Java, JavaFX Support.

2. Cách chạy test chương trình (Lỗi k có file gradlew.bat)
- Nhìn sang sidebar bên trái chọn biểu tượng Gradle (con voi)
- Chọn BlueMoon/Tasks/application/run -> chọn Run
- Sau khi ấn Run giao diện của chương trình sẽ hiện ra 

3. Cấu trúc thư mục và chức năng (Không được code nhầm chỗ)
src\main
    java\com\bluemoon\app
        application: Thiết lập cửa dổ ban đầu, tải file giao diện, không chứa logic nghiệp vụ
        controllers: Cầu nối giữa giao diện(file .fxml) và dữ liệu (service), lắng nghe người dùng từ giao diện và chuyển yêu cầu sang service để thực hiện yêu cầu tương ứng trên DB
        models: Chứa class dại diện các thực thể trong CSDL, biến và các hàm getter/setter, dùng để chuyển dữ liệu từ DB lên giao diện và ngược lại 
        services: Kết nối tới CSDL, chứa các class xử lý tính toán, truy vấn SQL, controller gọi sang đây để lấy dữ liệu
        utils: Chứa công cụ dùng chung cho dự án (Thường về định dạng)
    resources
        com\bluemoon\views: Chứa file giao diện .fxml
        css: Chứa file css để điều chỉnh file fxml

4. Cấu trúc Database
- Bảng users (Dùng cho Login)
+ id
+ username
+ password
+ role (Admin/ Kế toán)
+ fullname

- Bảng ho_khau (Dùng cho quản lí thông tin hộ dân)
+ maHoKhau (PM)
+ chuHo
+ diaChi
+ dienTich

- Bảng khoan_thu (Quản lí khoản thu gồm các loại phí)
+ id
+ tenKhoanThu
+ soTien
+ loaiKhoanThu (bắt buộc/ tự nguyện)

- Bảng nop_tien (Lịch sử thu phí)
+ id
+ maHoKhau (FK)
+ idKhoanThu (FK)
+ soTienNop
+ ngayNop

5. Chi tiết công việc
- Minh Anh: Lấy username/password từ giao diện, gọi MysqlConnection để so sánh với bảng users trong DB.
- Bằng: 
• Hoàn thiện class KhoanThuModel.java (khai báo các trường: id, tên, số tiền, loại phí).
• Viết class KhoanThuService.java để thực hiện các lệnh thêm, sửa, xóa khoản thu vào CSDL.
- Lê Hiếu: 
• Cài Scene Builder
• Chỉnh Login.fxml cho đẹp hơn (sử dụng Scene Builder)
• Tạo thêm file MainView.fxml là giao diện sau khi login, file KhoanThuView.fxml là giao diện quản lí chi phí
