package authen;

import com.microsoft.playwright.*;
import org.testng.annotations.Test;
import java.nio.file.Paths;

public class SetupAuthTest {
    @Test
    public void saveStorageState() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // 1. Đi tới trang login
            page.navigate("https://www.saucedemo.com/");

            // 2. Thực hiện đăng nhập
            page.locator("[data-test='username']").fill("standard_user");
            page.locator("[data-test='password']").fill("secret_sauce");
            page.locator("[data-test='login-button']").click();

            // 3. Kiểm tra đã vào được trang chủ chưa
            if (page.locator(".inventory_list").isVisible()) {
                // 4. Lưu trạng thái vào file auth.json
                context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("auth.json")));
                System.out.println(">>> Đã lưu session vào file auth.json");
            }

            browser.close();
        }
    }
}