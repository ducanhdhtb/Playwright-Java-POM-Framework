
---

#  Playwright Java Automation Framework

Dự án này là một Framework kiểm thử tự động (Automation Testing) hoàn chỉnh sử dụng **Playwright** với ngôn ngữ lập trình **Java**. Framework được xây dựng theo mô hình **Page Object Model (POM)**, tích hợp báo cáo **Allure**, hệ thống **Logging** chuyên nghiệp và tự động **Ghi hình/Chụp ảnh** khi bài test bị lỗi.

## 🛠 Công nghệ sử dụng

* **Language:** Java 11+
* **Engine:** Playwright (Web Automation)
* **Test Runner:** TestNG
* **Report:** Allure Report
* **Logging:** Log4j2
* **Build Tool:** Maven

---

## Cấu trúc Framework

```text
src/test/java/
├── pages/          # Page Object Model: Quản lý Locators và Actions
├── tests/          # Quản lý kịch bản test và BaseTest
└── utils/          # Các tiện ích: ConfigReader, TestListener (Screenshot failure)

src/test/resources/
├── config.properties   # Cấu hình môi trường (URL, Browser, Headless...)
└── log4j2.xml          # Cấu hình hệ thống Logging

```

---

## Các tính năng nổi bật

* **Page Object Model (POM):** Tách biệt rõ ràng giữa kịch bản test và các thành phần giao diện.
* **Allure Reporting:** Báo cáo HTML trực quan với biểu đồ và các bước thực thi chi tiết.
* **Automatic Screenshot:** Tự động chụp ảnh màn hình ngay khi bài test bị **Fail** thông qua TestNG Listeners.
* **Video Recording:** Tự động ghi lại video quá trình chạy test cho từng kịch bản.
* **Data-Driven:** Quản lý tham số môi trường dễ dàng thông qua file cấu hình `.properties`.
* **Professional Logging:** Theo dõi luồng chạy thông qua Log4j2 với các mức độ INFO, ERROR, WARN.

---

## Hướng dẫn cài đặt và chạy

### 1. Yêu cầu hệ thống

* Đã cài đặt **JDK 11** trở lên.
* Đã cài đặt **Maven**.
* (Tùy chọn) IDE: IntelliJ IDEA.

### 2. Cài đặt

```bash
# Clone dự án từ GitHub
git clone <URL_CUA_MAY>

# Tải các thư viện cần thiết
mvn clean install -DskipTests

```

### 3. Chạy kiểm thử và xem báo cáo

```bash
# Bước 1: Chạy toàn bộ test kịch bản
mvn clean test

# Bước 2: Sinh báo cáo Allure và mở trên trình duyệt
mvn allure:serve

```

---

## 📸 Kết quả

Sau khi chạy xong, kết quả sẽ bao gồm:

1. **Logs:** Được in tại Console và lưu trong thư mục `target/logs`.
2. **Videos:** Lưu trữ tại `target/videos` định dạng `.webm`.
3. **Report:** Báo cáo tổng hợp tại giao diện Allure (đính kèm Screenshot nếu có lỗi).

---

### Mày nên làm gì tiếp theo?

* **Thêm ảnh chụp màn hình:** Mày nên chạy bài test, chụp lại cái ảnh giao diện **Allure Report** rồi đẩy lên Git, sau đó chèn link ảnh vào file README này cho nó "ngầu".
* **Tài liệu Manual:** Như mày đã yêu cầu trước đó, mày có muốn tao tạo luôn file **PDF về kiến thức Manual Testing (Scrum, Test Case, Bug Report)** để mày đính kèm vào phần tài liệu học tập trong dự án này không?

Mày thấy file README này đã đủ "cháy" chưa? Cần thêm bớt mục nào cứ bảo tao nhé!
