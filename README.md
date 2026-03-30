# 📅 Ứng dụng Nhắc nhở Lịch trình (Schedule Reminder)

![Android Badge](https://img.shields.io/badge/Platform-Android-brightgreen.svg) ![Kotlin Badge](https://img.shields.io/badge/Language-Kotlin-orange.svg) ![Compose Badge](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg) ![Room Badge](https://img.shields.io/badge/Database-Room-blue.svg)

**Nhắc nhở Lịch trình** là một ứng dụng di động hiện đại được xây dựng trên nền tảng Android, giúp người dùng quản lý thời gian và công việc cá nhân một cách hiệu quả. Với giao diện "cute", thân thiện và công nghệ mới nhất, ứng dụng mang lại trải nghiệm nhắc nhở mượt mà và trực quan.

---

## ✨ Tính năng nổi bật

-   **Quản lý Lịch trình:** Thêm, sửa, xóa các tác vụ công việc hàng ngày một cách nhanh chóng.
-   **Lọc theo Thời gian:** Tích hợp bộ lịch (Calendar) giúp xem danh sách công việc theo từng ngày cụ thể.
-   **Phân loại Công việc:** Hỗ trợ nhiều danh mục (Công việc, Cá nhân, Sức khỏe, Khác) với các icon đặc trưng.
-   **Thông báo Thông minh:** Sử dụng `AlarmManager` để gửi thông báo đẩy (Push Notification) chính xác đến từng phút, ngay cả khi ứng dụng đã đóng.
-   **Giao diện Hiện đại:** Được thiết kế hoàn toàn bằng **Jetpack Compose** với phong cách Material Design 3, bo tròn mềm mại và hiệu ứng chuyển động mượt mà.
-   **Lưu trữ Offline:** Toàn bộ dữ liệu được lưu trữ an toàn trên thiết bị thông qua **Room Database**.

---

## 🛠 Công nghệ sử dụng

Ứng dụng được phát triển với các công nghệ Android hiện đại nhất hiện nay:

-   **Ngôn ngữ:** [Kotlin](https://kotlinlang.org/)
-   **Bộ công cụ giao diện:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Declarative UI)
-   **Kiến trúc:** MVVM (Model-View-ViewModel)
-   **Cơ sở dữ liệu:** Room Persistence Library
-   **Xử lý tác vụ nền:** AlarmManager & BroadcastReceiver
-   **Thư viện hỗ trợ:** 
    -   Coroutines & Flow (Xử lý bất đồng bộ)
    -   Material Design 3 (Thành phần UI)
    -   Splash Screen API (Màn hình khởi động)

---

## 📸 Ảnh chụp màn hình

*(Lưu ý: Bạn hãy chụp ảnh thật của ứng dụng và thay thế các link dưới đây)*

| Màn hình chính | Thêm lịch trình | Chọn giờ |
| :---: | :---: | :---: |
| ![Main Screen](https://via.placeholder.com/200x400?text=Main+Screen) | ![Add Task](https://via.placeholder.com/200x400?text=Add+Task) | ![Time Picker](https://via.placeholder.com/200x400?text=Time+Picker) |

---

## 📁 Cấu trúc thư mục chính

```
app/src/main/java/com/example/nhcnhlchtrnh/
├── data/               # Thực thể dữ liệu, DAO và Room Database
├── ui/                 # Giao diện Compose, ViewModel và các thành phần UI
├── utils/              # Các lớp hỗ trợ (Alarm, Constants, Helpers)
├── AlarmReceiver.kt    # Xử lý nhận tín hiệu báo thức và hiển thị Notification
```

---

## 🚀 Hướng dẫn cài đặt

1.  **Yêu cầu:** Android Studio Koala | 2024.1.1 trở lên.
2.  **Clone dự án:**
    ```sh
    git clone https://github.com/your-username/Nhcnhlchtrnh.git
    ```
3.  **Mở dự án:** Chọn `Open` trong Android Studio và tìm đến thư mục vừa clone.
4.  **Sync Gradle:** Chờ Android Studio tải các thư viện cần thiết.
5.  **Run:** Nhấn nút `Run` (phím tắt `Shift + F10`) để chạy ứng dụng trên máy ảo hoặc thiết bị thật (Android 8.0+).

---




