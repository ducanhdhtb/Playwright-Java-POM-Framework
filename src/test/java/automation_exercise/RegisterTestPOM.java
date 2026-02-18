package automation_exercise;

import automation_exercise.pages.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.ITestResult;
import org.testng.annotations.*;
import java.nio.file.Paths;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegisterTestPOM {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    // Khai báo các trang
    private HomePage homePage;
    private SignupLoginPage signupLoginPage;
    private AccountInformationPage accountPage;

    @BeforeClass
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(2000));
    }

    @BeforeMethod
    public void createContext() {
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
        page = context.newPage();

        // Khởi tạo đối tượng page
        homePage = new HomePage(page);
        signupLoginPage = new SignupLoginPage(page);
        accountPage = new AccountInformationPage(page);
    }

    @Test
    public void testRegisterUser() {
        homePage.navigate();
        homePage.clickSignupLogin();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String name = "Nguyễn Đức Anh";
        signupLoginPage.fillSignupForm(name, "auto_test" + timestamp + "@example.com");
        signupLoginPage.clickSignupButton();

        accountPage.fillAccountDetails("ducanh123", "15", "2", "1993");
        accountPage.fillAddressDetails("Nguyễn Đức", "Anh", "Techcombank", "119 Trần Duy Hưng", "United States", "84", "Washington", "98", "0385672074");
        accountPage.clickCreateAccount();

        assertThat(page.getByText("Account Created!")).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();

        assertThat(page.locator("#header")).containsText("Logged in as " + name);

        homePage.deleteAccount();
        assertThat(page.getByText("Account Deleted!")).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String tracePath = "traces/register_pom_" + System.currentTimeMillis() + ".zip";
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
        context.close();
    }

    @AfterClass
    public void closeBrowser() {
        browser.close();
        playwright.close();
    }
}