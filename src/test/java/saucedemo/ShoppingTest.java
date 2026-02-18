package saucedemo;

import com.microsoft.playwright.*;
import org.testng.ITestResult;
import org.testng.annotations.*;
import java.nio.file.Paths;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ShoppingTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeClass
    public void init() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeMethod
    public void setup() {
        // 1. Nạp file auth.json để bỏ qua bước Login
        context = browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(Paths.get("auth.json")));

        // 2. BẬT TRACING
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();
    }

    @Test
    public void buyProductTest() {
        page.navigate("https://www.saucedemo.com/inventory.html");

        // Thêm sản phẩm "Backpack" vào giỏ
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();

        // Vào giỏ hàng
        page.locator(".shopping_cart_link").click();
        assertThat(page.locator(".inventory_item_name")).hasText("Sauce Labs Backpack");

        // Nhấn Checkout
        page.locator("[data-test='checkout']").click();

        // Điền thông tin (Cố tình để trống một trường để test fail nếu muốn thử Trace)
        page.locator("[data-test='firstName']").fill("Nguyễn Đức");
        page.locator("[data-test='lastName']").fill("Anh");
        page.locator("[data-test='postalCode']").fill("10000");
        page.locator("[data-test='continue']").click();

        // Finish
        page.locator("[data-test='finish']").click();
        assertThat(page.locator(".complete-header")).hasText("Thank you for your order!");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // DỪNG TRACING: Lưu file zip nếu test bị lỗi
        String tracePath = "traces/" + result.getName() + ".zip";
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get(tracePath)));

        System.out.println("🔍 Nếu lỗi, kiểm tra trace tại: " + tracePath);
        context.close();
    }

    @AfterClass
    public void close() {
        browser.close();
        playwright.close();
    }
}