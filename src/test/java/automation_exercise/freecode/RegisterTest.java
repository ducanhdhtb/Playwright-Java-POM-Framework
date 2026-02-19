package automation_exercise.freecode;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.ITestResult;
import org.testng.annotations.*;
import java.nio.file.Paths;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegisterTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeClass
    public void setup() {
        playwright = Playwright.create();
        // setHeadless(false) để bạn nhìn thấy trình duyệt chạy
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(2000));
    }

    @BeforeMethod
    public void createContext() {
        context = browser.newContext();
        // Bật Trace Viewer để soi lại nếu test fail
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
    }

    @Test
    public void testRegisterUser() {
        // 1. Điều hướng đến trang chủ
        page.navigate("https://automationexercise.com/");
        assertThat(page).hasTitle("Automation Exercise");

        // 2. Click vào nút 'Signup / Login'
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Signup / Login")).click();

        // 3. Kiểm tra tiêu đề 'New User Signup!' có hiển thị không
        assertThat(page.locator(".signup-form h2")).hasText("New User Signup!");

        // 4. Nhập tên và email (Sử dụng timestamp để tránh trùng lặp email)
        String timestamp = String.valueOf(System.currentTimeMillis());
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill("Nguyễn Đức Anh");
        page.locator("form").filter(new Locator.FilterOptions().setHasText("Signup")).getByPlaceholder("Email Address").fill("auto_test" + timestamp + "@example.com");;


        // 5. Click 'Signup'
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Signup")).click();

        // 6. Điền thông tin chi tiết (Account Information)
        page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Mr.")).check(); // Chọn Mr.
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name *").setExact(true)).fill("Nguyễn Đưc Anh");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password *")).fill("ducanh123");
        page.locator("#days").selectOption("15");
        page.locator("#months").selectOption("2");
        page.locator("#years").selectOption("1993");

        // 7. Chọn checkboxes
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Sign up for our newsletter!")).check();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Receive special offers from")).check();

        // 8. Điền Address Information
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First name *")).fill("Nguyễn Đức");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last name *")).fill("Anh");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Company").setExact(true)).fill("Techcombank");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Address * (Street address, P.")).fill("119 Trần Duy hưng");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Address 2")).fill("Hoàn kiếm");
        page.getByLabel("Country *").selectOption("United States");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("State *")).fill("84");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("City * Zipcode *")).fill("Washington");
        page.locator("#zipcode").fill("98");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mobile Number *")).fill("0385672074");

        // 9. Click 'Create Account'
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Account")).click();

        // 10. Xác nhận thông báo thành công
        assertThat(page.getByText("Account Created!")).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();

        // 11. Kiểm tra 'Logged in as username' hiển thị ở menu
        assertThat(page.locator("#header")).containsText("Logged in as Nguyễn Đưc Anh");

        // 12. (Optional) Xóa tài khoản để dọn dẹp data
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(" Delete Account")).click();
        page.getByText("Account Deleted!").click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Lưu Trace nếu test Fail
        String tracePath = "traces/register_" + System.currentTimeMillis() + ".zip";
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
        context.close();
    }

    @AfterClass
    public void closeBrowser() {
        browser.close();
        playwright.close();
    }
}