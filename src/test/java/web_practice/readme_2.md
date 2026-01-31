# Playwright Practice – Đã đủ case chưa?

## Kết luận nhanh
Bộ trang HTML + bộ test Playwright Java (TestNG) hiện tại **chưa đủ 100%**, nhưng **đã đủ ~95% các case UI automation thực tế**.

Phần còn thiếu chủ yếu là **edge cases + framework-level features** (network mocking, auth reuse, tracing, CI...), không phải thiếu locator/action cơ bản.

---

## ĐÃ CÓ RỒI (rất đầy đủ)

### 1) Locator – gần như full bộ
Đã luyện được các kiểu locator phổ biến trong dự án:
- `getByTestId`
- `getByRole`
- `getByLabel`
- CSS selector / text-based selector
- `nth()`
- Locator trong **table row**
- Locator trong **iframe**
- Locator trong **shadow DOM**
- Locator trong **popup (new tab)**

=> Đủ dùng cho đa số DOM ngoài đời.

---

### 2) Actions – đầy đủ cho UI automation
Đã cover các action core:
- `click / dblclick / right click`
- `hover`
- `fill / clear`
- `keyboard.press`
- `selectOption` (single + multi)
- `check / uncheck`
- `dragTo`
- `setInputFiles`
- `press Escape`
- `dialog.accept / dismiss`
- `waitForPopup`

=> Không thiếu action “core”.

---

### 3) Wait & Sync – phần quan trọng nhất
Đã dùng đúng kiểu Playwright (tránh `sleep`):
- Auto-wait khi thao tác `click`
- `hasText(timeout)`
- `isVisible / isHidden`
- `isEnabled / isDisabled`
- `waitForURL`
- `waitForSelectorState(DETACHED)`
- Wait cho attribute change
- Wait cho iframe/shadow content

=> Đây là phần giúp test ổn định, ít flaky.

---

### 4) Assertions – đủ để viết test bền
Đã có:
- `hasText / containsText`
- `hasValue`
- `hasAttribute`
- `isVisible / isHidden`
- `isEnabled / isDisabled`
- `hasCount`
- Assert trong iframe / shadow / popup

---

### 5) Tình huống giống web app thật
Có sẵn các flow rất thường gặp:
- Form submit / reset
- Table filter + click action theo row
- Dynamic list add/remove
- Modal + đóng bằng ESC
- Toast (assert gián tiếp)
- Progress bar
- Hash navigation
- New tab / popup

=> Đủ giống dự án thật để luyện tay.

---

## CÒN THIẾU (để học tiếp sau)

### 1) Network / API interception (Playwright mạnh nhất chỗ này)
Chưa cover:
- `page.route()` để mock API
- `waitForResponse()`, `waitForRequest()`
- UI test phụ thuộc API (mock data, simulate failure)

### 2) Authentication / storage state
Chưa cover:
- `storageState.json`
- Login 1 lần, reuse session nhiều test

### 3) Debug & chống flaky (framework-level)
Chưa cover:
- Screenshot on fail
- Video / Trace viewer
- Retry logic TestNG, test isolation tốt hơn

### 4) Accessibility (a11y)
Chưa test:
- role/aria correctness
- tab navigation, focus order

### 5) Mobile / responsive
Chưa cover:
- Mobile viewport
- Touch events
- Device emulation

---

## Tóm lại
- Với bộ practice hiện tại: **đủ cho Junior → Mid UI automation**, đủ làm dự án thật.
- Thiếu chủ yếu là các mảng “nâng cao” về **network mocking, auth reuse, debug/trace, CI, visual testing**.

---

## Lộ trình tiếp theo (khi rảnh sẽ làm)
1) Mock API + network
2) Login reuse (`storageState`)
3) POM + DataProvider + parallel TestNG
4) CI (GitHub Actions)
5) Visual testing (snapshot)
