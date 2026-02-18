package authen;
import com.microsoft.playwright.*;
import org.testng.annotations.*;
import java.nio.file.Paths;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class SauceInventoryTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeClass
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000));

        // NẠP SESSION TỪ FILE JSON
        context = browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(Paths.get("auth.json")));
    }

    @BeforeMethod
    public void createPage() {
        page = context.newPage();
    }

    @Test
    public void testAddToCart() {
        // Vào thẳng trang inventory (không bị đá ra trang login nhờ auth.json)
        page.navigate("https://www.saucedemo.com/inventory.html");

        // Kiểm tra xem có đúng là đã đăng nhập không
        assertThat(page.locator(".title")).hasText("Products");

        // Click thêm sản phẩm đầu tiên vào giỏ
        page.locator("button[data-test^='add-to-cart']").first().click();

        // Kiểm tra badge giỏ hàng hiện số 1
        assertThat(page.locator(".shopping_cart_badge")).hasText("1");
    }

    @Test
    public void testLogout() {
        page.navigate("https://www.saucedemo.com/inventory.html");

        // Mở menu và nhấn Logout
        page.click("#react-burger-menu-btn");
        page.click("#logout_sidebar_link");

        // Xác nhận đã quay về trang login
        assertThat(page).hasURL("https://www.saucedemo.com/");
    }

    @AfterClass
    public void tearDown() {
        browser.close();
        playwright.close();
    }
}