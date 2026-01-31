package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PracticePage {
    private static final Logger log = LogManager.getLogger(PracticePage.class);
    private final Page page;

    // 1. Khai báo Locators (Sử dụng các phương thức getBy của Playwright)
    private final Locator nameInput;
    private final Locator maleRadio;
    private final Locator roleDropdown;
    private final Locator subscribeCheckbox;
    private final Locator submitBtn;

    // 2. Constructor: Khởi tạo các Locator ngay khi object được tạo
    public PracticePage(Page page) {
        this.page = page;
        this.nameInput = page.getByPlaceholder("Nhập tên của bạn");
        this.maleRadio = page.locator("#male"); // Dùng locator ID vì radio này có thể thiếu label chuẩn
        this.roleDropdown = page.getByLabel("Vai trò:");
        this.subscribeCheckbox = page.getByRole(AriaRole.CHECKBOX);
        this.submitBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Gửi thông tin"));
    }

    // 3. Các hành động (Actions)
    public void fillInformation(String name, String roleValue) {
        log.info("Nhập tên: {}", name);
        nameInput.fill(name);

        log.info("Chọn giới tính Nam");
        maleRadio.check();

        log.info("Chọn vai trò: {}", roleValue);
        roleDropdown.selectOption(roleValue);

        log.info("Tích chọn đồng ý nhận thông báo");
        subscribeCheckbox.check();
    }

    public void clickSubmit() {
        log.info("Bấm nút Gửi thông tin");
        submitBtn.click();
    }
}