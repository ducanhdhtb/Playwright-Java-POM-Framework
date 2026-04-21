# TEST PLAN
## Project: Automation Exercise — Playwright Java POM Framework
**Version:** 2.0  
**Ngày:** 2026-04-21  
**Tác giả:** QA Team  
**Reviewed by:** Senior QA 

---

## 1. THÔNG TIN DỰ ÁN

| Mục | Chi tiết |
|-----|---------|
| **Application Under Test** | https://automationexercise.com |
| **Loại ứng dụng** | E-commerce Web Application |
| **Framework** | Playwright Java 1.49.0 + TestNG 7.8.0 |
| **Ngôn ngữ** | Java 17 |
| **Build Tool** | Maven 3.x |
| **Reporting** | Allure 2.24.0 |
| **CI/CD** | Jenkins (Jenkinsfile) |
| **Branch** | dev_jenkin |

---

## 2. MỤC TIÊU KIỂM THỬ

### 2.1 Mục tiêu chính
- Xác minh toàn bộ **26 test cases** chính thức từ automationexercise.com hoạt động đúng
- Xác minh **14 API endpoints** theo tài liệu API list
- Đảm bảo các luồng nghiệp vụ quan trọng (đăng ký, đăng nhập, mua hàng) không bị regression
- Phát hiện lỗi sớm thông qua CI/CD pipeline tự động

### 2.2 Mục tiêu phụ
- Đảm bảo UI responsive và các element hiển thị đúng
- Xác minh tính toàn vẹn dữ liệu giữa UI và API
- Kiểm tra các negative scenarios (input sai, thiếu tham số, method không hỗ trợ)

---

## 3. PHẠM VI KIỂM THỬ

### 3.1 IN SCOPE (Trong phạm vi)

#### UI Testing
| Module | Test Cases | Mô tả |
|--------|-----------|-------|
| **User Registration** | TC1, TC5 | Đăng ký mới, đăng ký email trùng |
| **User Login/Logout** | TC2, TC3, TC4, TC20, TC21 | Login đúng/sai, logout, negative cases |
| **Account Management** | TC24, TC29 | Quản lý tài khoản, xác minh địa chỉ |
| **Product Browsing** | TC8, TC9, TC22 | Xem sản phẩm, tìm kiếm, lọc |
| **Category & Brand** | TC18, TC19 | Duyệt theo danh mục, thương hiệu |
| **Cart Management** | TC12, TC13, TC17, TC23 | Thêm/xóa sản phẩm, số lượng |
| **Checkout & Order** | TC14, TC15, TC16, TC30 | Đặt hàng, thanh toán, tải hóa đơn |
| **Subscription** | TC10, TC11 | Đăng ký newsletter |
| **Contact Us** | TC6 | Form liên hệ |
| **Navigation** | TC7, TC25, TC26 | Test Cases page, scroll up/down |
| **Product Review** | TC27 | Viết đánh giá sản phẩm |
| **Recommended Items** | TC28 | Thêm sản phẩm gợi ý vào giỏ |
| **E2E Flow** | E2EPurchaseTest | Luồng mua hàng đầu đủ |

#### API Testing
| API | Endpoint | Method | Mô tả |
|-----|---------|--------|-------|
| API 1 | /api/productsList | GET | Lấy danh sách sản phẩm |
| API 2 | /api/productsList | POST | Phương thức không hỗ trợ → 405 |
| API 3 | /api/brandsList | GET | Lấy danh sách thương hiệu |
| API 4 | /api/brandsList | PUT | Phương thức không hỗ trợ → 405 |
| API 5 | /api/searchProduct | POST | Tìm kiếm sản phẩm |
| API 6 | /api/searchProduct | POST | Thiếu tham số → 400 |
| API 7 | /api/verifyLogin | POST | Xác minh đăng nhập hợp lệ |
| API 8 | /api/verifyLogin | POST | Thiếu email → 400 |
| API 9 | /api/verifyLogin | DELETE | Phương thức không hỗ trợ → 405 |
| API 10 | /api/verifyLogin | POST | Thông tin sai → 404 |
| API 11 | /api/createAccount | POST | Tạo tài khoản mới |
| API 12 | /api/deleteAccount | DELETE | Xóa tài khoản |
| API 13 | /api/updateAccount | PUT | Cập nhật tài khoản |
| API 14 | /api/getUserDetailByEmail | GET | Lấy thông tin user |

### 3.2 OUT OF SCOPE (Ngoài phạm vi)
- Performance/Load testing
- Security penetration testing
- Mobile responsive testing
- Cross-browser testing (chỉ Chromium mặc định)
- Payment gateway thực tế (dùng test card)
- Email verification thực tế

---

## 4. PHƯƠNG PHÁP KIỂM THỬ

### 4.1 Test Levels

```
┌─────────────────────────────────────────────┐
│              E2E Tests (5%)                  │  ← Luồng mua hàng đầy đủ
├─────────────────────────────────────────────┤
│           UI Integration Tests (60%)         │  ← TC1-TC30
├─────────────────────────────────────────────┤
│              API Tests (35%)                 │  ← TC_API01-07
└─────────────────────────────────────────────┘
```

### 4.2 Test Types
| Loại | Mô tả | Tools |
|------|-------|-------|
| **Functional** | Kiểm tra chức năng theo spec | Playwright + TestNG |
| **Regression** | Đảm bảo không có breaking changes | TestNG groups |
| **Negative** | Input sai, thiếu tham số, edge cases | TestNG groups |
| **API** | Kiểm tra REST API endpoints | Playwright APIRequestContext |
| **E2E** | Luồng nghiệp vụ đầu đủ | Playwright |

### 4.3 Test Data Strategy
| Nguồn | Dùng cho |
|-------|---------|
| **Excel (AutomationTestData.xlsx)** | Data-driven tests (TC1-TC16) |
| **API Setup (UserApiHelper)** | Tạo/xóa user nhanh không qua UI |
| **Inline data** | TC đơn giản, không cần nhiều biến thể |
| **DataProvider** | TC22 (search keywords), TC_API06 |

---

## 5. TEST ENVIRONMENT

### 5.1 Môi trường
| Môi trường | URL | Mục đích |
|-----------|-----|---------|
| **Dev** | https://automationexercise.com | Chạy tất cả tests |
| **Staging** | config-staging.properties | Regression trước release |
| **Prod** | config-prod.properties | Smoke test sau deploy |

### 5.2 Cấu hình Browser
```properties
playwright.browser=chromium
playwright.headless=true          # CI/CD
playwright.headless=false         # Local debug
playwright.defaultTimeoutMs=30000
playwright.navigationTimeoutMs=60000
```

### 5.3 Yêu cầu hệ thống
- JDK 17+
- Maven 3.6+
- Node.js 18+ (Playwright browsers)
- RAM: tối thiểu 4GB
- OS: macOS / Linux / Windows

---

## 6. TEST EXECUTION

### 6.1 Test Suites

| Suite File | Mô tả | Thread | Thời gian ước tính |
|-----------|-------|--------|-------------------|
| `testng.xml` | Tất cả tests (sequential) | 1 | ~45 phút |
| `testng-parallel.xml` | Tất cả tests (parallel) | 4 | ~15 phút |
| `testng-api.xml` | API tests only | 3 | ~3 phút |
| `testng-cucumber.xml` | Cucumber BDD scenarios | 1 | ~50 phút |

### 6.2 Lệnh chạy

```bash
# Smoke tests (CI gate)
mvn test -Dtestng.groups=smoke

# Regression đầy đủ
mvn test -Dtestng.suiteXmlFile=testng-parallel.xml

# API tests only (nhanh, không cần browser)
mvn test -Dtestng.suiteXmlFile=testng-api.xml

# E2E tests
mvn test -Dtestng.groups=e2e

# Negative tests
mvn test -Dtestng.groups=negative

# Cucumber BDD
mvn test -Dtestng.suiteXmlFile=testng-cucumber.xml -Dcucumber.filter.tags="@smoke"
```

### 6.3 Test Execution Order (Priority)

```
1. API Tests (không cần browser, nhanh nhất)
   └── TC_API01 → TC_API07

2. Smoke Tests (critical path)
   └── TC1, TC2, TC3, TC9, TC10, TC11, TC12

3. Regression Tests
   └── TC4-TC8, TC13-TC19, TC22-TC30

4. E2E Tests (chậm nhất)
   └── TC14, TC15, TC16, TC29, TC30, E2EPurchaseTest
```

---

## 7. PHÂN TÍCH RỦI RO

| Rủi ro | Xác suất | Mức độ | Biện pháp |
|--------|---------|--------|----------|
| Site automationexercise.com bị overload (503) | Cao | Cao | RetryAnalyzer, SkipException cho 503 |
| Test data bị conflict (email trùng) | Trung bình | Trung bình | Dùng timestamp trong email |
| Flaky tests do timing | Trung bình | Trung bình | Tăng timeout, waitFor explicit |
| API thay đổi response format | Thấp | Cao | Kiểm tra field tồn tại, không hardcode |
| Browser crash trong parallel | Thấp | Trung bình | @BeforeMethod tạo context mới |
| Download invoice timeout | Trung bình | Thấp | waitForDownload với timeout |

---

## 8. ENTRY/EXIT CRITERIA

### 8.1 Entry Criteria (Điều kiện bắt đầu)
- [ ] Code đã được merge vào branch test
- [ ] `mvn clean test-compile` thành công
- [ ] Playwright browsers đã được cài đặt
- [ ] Site automationexercise.com accessible (HTTP 200)
- [ ] Excel test data file tồn tại

### 8.2 Exit Criteria (Điều kiện kết thúc)
- [ ] Tất cả **smoke tests** PASS (100%)
- [ ] **Regression tests** PASS ≥ 95%
- [ ] **API tests** PASS ≥ 98%
- [ ] Không có **Critical/Blocker** bugs mở
- [ ] Allure report đã được generate và review

---

## 9. DELIVERABLES

| Artifact | Location | Mô tả |
|---------|---------|-------|
| Test Scripts | `src/test/java/` | 57 test methods |
| Feature Files | `src/test/resources/features/` | 8 Cucumber feature files |
| Test Data | `src/test/resources/AutomationTestData.xlsx` | Excel data |
| Allure Report | `target/allure-results/` | HTML report |
| Surefire Reports | `target/surefire-reports/` | XML/HTML |
| Videos | `target/videos/` | Retain on failure |
| Traces | `traces/` | Playwright trace files |
| Test Plan | `TEST_PLAN.md` | Tài liệu này |
| Test Strategy | `TEST_STRATEGY.md` | Chiến lược kiểm thử |

---

## 10. LỊCH THỰC HIỆN

| Giai đoạn | Hoạt động | Thời gian |
|----------|----------|----------|
| **Sprint 1** | Setup framework, TC1-TC10 | Tuần 1 |
| **Sprint 2** | TC11-TC20, API layer | Tuần 2 |
| **Sprint 3** | TC21-TC30, Cucumber BDD | Tuần 3 |
| **Sprint 4** | CI/CD integration, healing, documentation | Tuần 4 |
| **Ongoing** | Maintenance, new TC khi site update | Liên tục |

---

## 11. TEAM & TRÁCH NHIỆM

| Vai trò | Trách nhiệm |
|--------|------------|
| **QA Lead** | Review test plan, approve exit criteria |
| **QA Engineer** | Viết và maintain test scripts |
| **DevOps** | Cấu hình Jenkins pipeline |
| **Developer** | Fix bugs được phát hiện |

---

*Document này được generate tự động dựa trên codebase thực tế của project.*
