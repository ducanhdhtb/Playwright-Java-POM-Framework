
---

# Playwright Java Automation Framework

Dự án này là framework automation dùng **Playwright Java + TestNG**, theo mô hình **Page Object Model (POM)**, có **Allure report**, chạy **data-driven từ Excel**, và cơ chế lưu **trace/video/screenshot** khi cần debug.

## 🛠 Công nghệ sử dụng

* **Language:** Java 17+
* **Engine:** Playwright (Web Automation)
* **Test Runner:** TestNG
* **Report:** Allure Report
* **Logging:** Log4j2
* **Build Tool:** Maven

---

## Cấu trúc Framework (chính)

```text
src/main/java/pages/                     # Page Objects
src/test/java/automation_exercise/        # BaseTest/BaseApiTest
src/test/java/automation_exercise/tests/ui/   # UI test cases (TC* + E2E)
src/test/java/automation_exercise/tests/api/  # API test cases (TC_API*)
src/test/java/api/                        # API client + helpers (Playwright APIRequestContext)
src/test/java/utils/                      # Config, listeners, Excel helpers

src/test/resources/
├── config.properties                 # baseUrl + test defaults
├── playwright.properties             # browser/timeouts/tracing/video/allure toggles
├── AutomationTestData.xlsx           # data theo từng test case (TC1, TC2, ...)
└── log4j2.xml                        # logging

```

---

## Các tính năng nổi bật

* **Page Object Model (POM):** Tách biệt rõ ràng giữa kịch bản test và các thành phần giao diện.
* **Allure Reporting:** Báo cáo HTML trực quan với biểu đồ và các bước thực thi chi tiết.
* **Allure Steps + Screenshot mỗi step (tùy chọn):** bật/tắt bằng `playwright.properties` hoặc ENV/JVM.
* **Video/Tracing retain-on-failure:** lưu artifact khi fail, dọn video khi pass.
* **Data-Driven từ Excel:** mỗi test case map vào 1 sheet `TCx`.
* **Professional Logging:** Theo dõi luồng chạy thông qua Log4j2 với các mức độ INFO, ERROR, WARN.

---

## Hướng dẫn cài đặt và chạy

### 1. Yêu cầu hệ thống

* Đã cài đặt **JDK 17** trở lên.
* Đã cài đặt **Maven**.
* (Tùy chọn) IDE: IntelliJ IDEA.

### 2. Cài đặt

```bash
# Clone dự án từ GitHub
git clone <URL_CUA_MAY>

# Tải các thư viện cần thiết
mvn clean install -DskipTests

```

### 3. Chạy test (groups/suite) và xem báo cáo

```bash
# Chạy toàn bộ test
mvn clean test

# Chạy smoke (nhanh)
mvn test -Dtestng.suiteXmlFile=testng-smoke.xml
# hoặc (cách cũ)
# mvn test -Dtestng.groups=smoke

# Chạy regression
mvn test -Dtestng.suiteXmlFile=testng-regression.xml
# hoặc (cách cũ)
# mvn test -Dtestng.groups=regression

# Chạy e2e
mvn test -Dtestng.suiteXmlFile=testng-e2e.xml
# hoặc (cách cũ)
# mvn test -Dtestng.groups=e2e

# Chạy song song (TestNG suite riêng)
mvn test -Dtestng.suiteXmlFile=testng-parallel.xml

# Chạy API-only (không browser)
mvn test -Dtestng.suiteXmlFile=testng-api.xml

# Sinh báo cáo Allure và mở trên trình duyệt
mvn allure:serve

```

---

## 📸 Kết quả

Sau khi chạy xong, kết quả sẽ bao gồm:

1. **Logs:** Được in tại Console và lưu trong thư mục `target/logs`.
2. **Videos:** Lưu trữ tại `target/videos` định dạng `.webm`.
3. **Report:** Báo cáo tổng hợp tại giao diện Allure (đính kèm Screenshot nếu có lỗi).

---
