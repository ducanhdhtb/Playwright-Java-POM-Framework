package saucedemo;

import com.microsoft.playwright.*;
import org.testng.annotations.Test;
import java.nio.file.Paths;

public class AuthSetup {
    @Test
    public void setupSession() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("https://www.saucedemo.com/");
            page.locator("[data-test='username']").fill("standard_user");
            page.locator("[data-test='password']").fill("secret_sauce");
            page.locator("[data-test='login-button']").click();

            // Đợi trang chủ load xong rồi lưu session
            page.waitForURL("**/inventory.html");
            context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("auth.json")));

            System.out.println("✅ Đã tạo xong file auth.json!");
            browser.close();
        }
    }
}