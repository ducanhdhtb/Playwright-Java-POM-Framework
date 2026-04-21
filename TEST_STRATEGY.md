# TEST STRATEGY
## Project: Automation Exercise — Playwright Java POM Framework
**Version:** 2.0  
**Ngày:** 2026-04-21  
**Loại tài liệu:** Chiến lược kiểm thử tổng thể

---

## 1. TỔNG QUAN CHIẾN LƯỢC

Framework này áp dụng chiến lược **"API-First, UI-Verify"** — tức là:
- Dùng **API** để setup/teardown test data (nhanh, ổn định)
- Dùng **UI** để verify business logic và user experience
- Dùng **E2E** chỉ cho các luồng nghiệp vụ quan trọng nhất

```
┌──────────────────────────────────────────────────────────┐
│                    TEST PYRAMID                           │
│                                                           │
│                    ┌─────────┐                            │
│                    │   E2E   │  5%  — 5 tests             │
│                  ┌─┴─────────┴─┐                          │
│                  │  UI Tests   │  60% — 30 tests           │
│               ┌──┴─────────────┴──┐                       │
│               │    API Tests      │  35% — 22 tests        │
│            └──────────────────────┘                       │
└──────────────────────────────────────────────────────────┘
```

---

## 2. KIẾN TRÚC FRAMEWORK

### 2.1 Layer Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    TEST LAYER                            │
│  tests/ui/TC*.java          tests/api/TC_API*.java       │
│  (Playwright Browser)       (APIRequestContext)          │
├─────────────────────────────────────────────────────────┤
│                   BASE LAYER                             │
│  BaseTest.java              BaseApiTest.java             │
│  (Browser lifecycle)        (API lifecycle, no browser)  │
├─────────────────────────────────────────────────────────┤
│                   PAGE OBJECT LAYER                      │
│  pages/*.java                                            │
│  (Encapsulate UI interactions)                           │
├─────────────────────────────────────────────────────────┤
│                   API LAYER                              │
│  api/ApiClient.java → api/UserApiHelper.java             │
│  (HTTP client, response wrapper, user CRUD)              │
├─────────────────────────────────────────────────────────┤
│                   UTILITY LAYER                          │
│  ConfigReader | ExcelReader | RetryAnalyzer              │
│  TestListener | StepLoggerAspect                         │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Design Patterns áp dụng

| Pattern | Áp dụng ở đâu | Lợi ích |
|---------|--------------|---------|
| **Page Object Model** | `pages/*.java` | Tách UI logic khỏi test logic |
| **Factory Method** | `BaseTest.setupBrowser()` | Tạo browser/context linh hoạt |
| **Builder** | `BrowserType.LaunchOptions` | Config browser dễ đọc |
| **Template Method** | `BaseTest` lifecycle | Chuẩn hóa setup/teardown |
| **Facade** | `UserApiHelper` | Đơn giản hóa API calls |
| **Strategy** | `RetryAnalyzer` | Retry logic có thể thay đổi |
| **Observer** | `TestListener` | Capture artifacts khi fail |
| **AOP** | `StepLoggerAspect` | Auto-log mọi @Step |

---

## 3. CHIẾN LƯỢC TEST DATA

### 3.1 Nguyên tắc

```
Rule 1: Mỗi test phải TỰ TẠO và TỰ XÓA data của mình
Rule 2: Không dùng shared/static test accounts
Rule 3: Email luôn unique bằng timestamp
Rule 4: API setup > UI setup (nhanh hơn 5-10x)
```

### 3.2 Data Sources

```
Excel (AutomationTestData.xlsx)
├── TC1  — Register user data
├── TC2  — Login credentials
├── TC3  — Invalid login data
├── TC5  — Existing email data
├── TC6  — Contact form data
├── TC9  — Search keywords
├── TC10 — Subscription emails
├── TC11 — Cart subscription emails
├── TC14 — Order with registration
├── TC15 — Order before checkout
├── TC16 — Order after login
└── E2EPurchase — Full E2E data

API Setup (UserApiHelper.setupUser)
├── TC20 — API-backed login test
├── TC21 — Negative login tests
├── TC23 — Cart after login
├── TC24 — Account management
└── TC29 — Address verification

Inline DataProvider
├── TC22 — Search keywords: top, dress, jeans
└── TC_API06 — Search: top, dress, tshirt, jean
```

### 3.3 Email Generation Pattern

```java
// Pattern: prefix_timestamp@domain.com
"user_" + System.currentTimeMillis() + "@example.com"    // UI tests
"api_"  + System.currentTimeMillis() + "@testmail.com"   // API tests
"addr_" + System.currentTimeMillis() + "@test.com"       // Address tests
```

---

## 4. CHIẾN LƯỢC THỰC HIỆN TEST

### 4.1 Test Execution Flow

```
┌─────────────────────────────────────────────────────────┐
│                  CI/CD PIPELINE                          │
│                                                          │
│  Push Code                                               │
│      │                                                   │
│      ▼                                                   │
│  mvn compile ──── FAIL ──→ Fix & Retry                   │
│      │                                                   │
│      ▼                                                   │
│  API Tests (testng-api.xml) ── ~3 min                    │
│      │                                                   │
│      ▼                                                   │
│  Smoke Tests (-Dtestng.groups=smoke) ── ~10 min          │
│      │                                                   │
│      ▼                                                   │
│  Regression Tests (testng-parallel.xml) ── ~15 min       │
│      │                                                   │
│      ▼                                                   │
│  Allure Report + Email Notification                      │
└─────────────────────────────────────────────────────────┘
```

### 4.2 Parallel Execution Strategy

```xml
<!-- testng-parallel.xml -->
parallel="classes" thread-count="4"
```

**Tại sao parallel="classes" thay vì "methods"?**
- Mỗi class có `@BeforeClass` tạo browser riêng → thread-safe
- Tránh race condition giữa các test methods trong cùng class
- Mỗi thread có `BrowserContext` độc lập

### 4.3 Retry Strategy

```java
// RetryAnalyzer.java
retryCount = 1                    // Retry 1 lần
retry.onlyTimeouts = true         // Chỉ retry TimeoutError
                                  // KHÔNG retry AssertionError
```

**Lý do:** Retry assertion failures sẽ che giấu bugs thật. Chỉ retry infrastructure failures (network timeout, browser crash).

---

## 5. CHIẾN LƯỢC OBSERVABILITY

### 5.1 Artifact Collection

```
Khi test PASS:
├── Allure report (steps + timing)
├── Log4j2 logs
└── Video: XÓA (tiết kiệm disk)

Khi test FAIL:
├── Allure report + Screenshot đính kèm
├── Page source HTML
├── Current URL
├── Playwright Trace (.zip) → mở bằng trace viewer
├── Video recording (.webm)
└── Log4j2 logs với stack trace
```

### 5.2 Allure Report Structure

```
Allure Report
├── Overview (pass/fail/skip stats)
├── Suites (theo class)
├── Behaviors (theo @Feature, @Story)
├── Timeline (execution timeline)
└── Per Test:
    ├── Steps (từ @Step annotation)
    ├── Screenshots (per step nếu bật)
    ├── Attachments (screenshot, URL, source khi fail)
    └── Parameters (từ DataProvider)
```

### 5.3 AspectJ Auto-Logging

```java
// StepLoggerAspect.java — tự động log mọi @Step method
@Around("@annotation(io.qameta.allure.Step)")
public Object logStep(ProceedingJoinPoint joinPoint) {
    // 1. Log step description với actual params
    // 2. Execute method
    // 3. Chụp screenshot nếu allure.step.screenshots=true
}
```

---

## 6. CHIẾN LƯỢC QUẢN LÝ LỖI

### 6.1 Phân loại lỗi

| Loại | Ví dụ | Xử lý |
|------|-------|-------|
| **Infrastructure** | Timeout, browser crash | Retry 1 lần |
| **Application Bug** | Wrong text, missing element | Fail ngay, tạo bug report |
| **Test Data** | Email trùng, user không tồn tại | Fix test setup |
| **Environment** | 503 server overload | Skip với SkipException |
| **Flaky** | Timing issue | Thêm explicit wait |

### 6.2 Self-Healing Approach

```
Test FAIL
    │
    ├── 503/429/502? → SkipException (không phải bug)
    │
    ├── TimeoutError? → RetryAnalyzer retry 1 lần
    │       │
    │       ├── Pass on retry → Flaky test, cần investigate
    │       └── Fail again → Real failure, report bug
    │
    └── AssertionError? → Fail ngay, không retry
            │
            └── Attach: screenshot + URL + page source
```

### 6.3 Debug Workflow

```bash
# 1. Xem Allure report
mvn allure:serve

# 2. Mở Playwright trace
npx playwright show-trace traces/testName_timestamp.zip

# 3. Chạy lại test cụ thể với headed mode
mvn test -Dplaywright.headless=false -Dtest=TC_ClassName#methodName

# 4. Bật step screenshots
mvn test -DALLURE_STEP_SCREENSHOTS=true
```

---

## 7. CHIẾN LƯỢC BDD (CUCUMBER)

### 7.1 Khi nào dùng Cucumber vs TestNG

| Dùng Cucumber | Dùng TestNG trực tiếp |
|--------------|----------------------|
| Stakeholder cần đọc test | Developer/QA internal |
| Acceptance criteria từ BA | Technical regression |
| Feature documentation | Performance-sensitive |
| Cross-team collaboration | Data-heavy tests |

### 7.2 Feature File Organization

```
features/
├── user_registration.feature    # @registration
├── user_login.feature           # @login
├── user_account.feature         # @account
├── product_browsing.feature     # @products
├── cart_management.feature      # @cart
├── checkout_and_order.feature   # @checkout @e2e
├── subscription.feature         # @subscription
└── contact_us.feature           # @contact
```

### 7.3 Step Sharing Strategy

```
ScenarioContext (shared state)
    │
    ├── CommonSteps      — navigate, click, assert
    ├── UserLoginSteps   — login, API user setup
    ├── UserRegSteps     — signup, account details
    ├── ProductSteps     — search, browse, detail
    ├── CartSteps        — add/remove, checkout
    ├── CheckoutSteps    — payment, order
    ├── ApiSteps         — all API assertions
    ├── SubscriptionSteps
    └── ContactUsSteps
```

---

## 8. CHIẾN LƯỢC CI/CD

### 8.1 Jenkins Pipeline Stages

```groovy
Init → Checkout → Install Browsers → Run Tests → Report → Notify
```

### 8.2 Configurable Parameters

```groovy
TEST_GROUPS      = "smoke"           // Mặc định chạy smoke
TESTNG_SUITE     = "testng.xml"      // Suite file
PLAYWRIGHT_HEADLESS = "true"         // Headless trong CI
EMAIL_TO         = "team@company.com"
```

### 8.3 Recommended CI Strategy

```
PR/Commit → Smoke Tests (10 min)
                │
                ▼
Daily Schedule → Full Regression (15 min parallel)
                │
                ▼
Pre-Release → E2E + API Tests
                │
                ▼
Post-Deploy → Smoke Tests (production verify)
```

---

## 9. METRICS & KPIs

| Metric | Target | Đo bằng |
|--------|--------|---------|
| **Smoke Pass Rate** | 100% | Allure / Jenkins |
| **Regression Pass Rate** | ≥ 95% | Allure / Jenkins |
| **API Pass Rate** | ≥ 98% | Allure / Jenkins |
| **Flaky Test Rate** | < 5% | Retry count tracking |
| **Test Execution Time** | < 20 min (parallel) | Jenkins build time |
| **Code Coverage (test)** | N/A (E2E framework) | — |
| **Bug Detection Rate** | Track per sprint | JIRA/bug tracker |

---

## 10. MAINTENANCE STRATEGY

### 10.1 Khi site thay đổi UI

```
1. Chạy smoke tests → xác định TC bị fail
2. Dùng MCP Playwright inspect element mới
3. Update locator trong Page Object (1 chỗ duy nhất)
4. Chạy lại TC bị fail → verify fix
5. Commit với message: "fix: update locator for [element]"
```

### 10.2 Khi thêm feature mới

```
1. Thêm test case vào Excel (nếu data-driven)
2. Tạo/update Page Object methods
3. Tạo TC_XX_NewFeature.java
4. Thêm vào TestData.java nếu cần DataProvider
5. Thêm Cucumber scenario vào feature file tương ứng
6. Update TEST_PLAN.md
```

### 10.3 Dependency Updates

```bash
# Kiểm tra outdated dependencies
mvn versions:display-dependency-updates

# Update Playwright (cần test lại toàn bộ)
# Update TestNG (cần verify annotations)
# Update Allure (cần verify report format)
```

---

## 11. TOOL STACK SUMMARY

| Tool | Version | Mục đích |
|------|---------|---------|
| Playwright Java | 1.49.0 | Browser automation + API testing |
| TestNG | 7.8.0 | Test runner, parallel, groups |
| Allure | 2.24.0 | Test reporting |
| AspectJ | 1.9.19 | AOP auto-logging |
| Log4j2 | 2.20.0 | Application logging |
| Apache POI | 5.2.3 | Excel data reading |
| Jackson | 2.15.2 | JSON parsing cho API |
| Cucumber | 7.14.0 | BDD framework |
| PicoContainer | 7.14.0 | DI cho Cucumber steps |
| Maven | 3.x | Build & dependency management |
| Jenkins | Latest | CI/CD pipeline |
| Allure CLI | 2.24.0 | Report generation |
| MCP Playwright | Latest | Browser control trong Kiro IDE |

---

*Tài liệu này phản ánh chiến lược thực tế được implement trong codebase, không phải lý thuyết.*
