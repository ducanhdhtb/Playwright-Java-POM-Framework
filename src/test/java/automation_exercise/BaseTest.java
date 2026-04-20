package automation_exercise;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pages.AccountInformationPage;
import pages.CartPage;
import pages.ContactUsPage;
import pages.HomePage;
import pages.PaymentPage;
import pages.ProductDetailPage;
import pages.ProductsPage;
import pages.SignupLoginPage;
import pages.CheckoutPage;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseTest {
    private static final List<String> DEFAULT_BROWSER_PATHS = List.of(
            "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
            "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
            "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe",
            "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe"
    );

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected HomePage homePage;
    protected SignupLoginPage signupLoginPage;
    protected AccountInformationPage accountPage;
    protected ProductsPage productsPage;
    protected CartPage cartPage;
    protected PaymentPage paymentPage;
    protected ContactUsPage contactPage;
    protected ProductDetailPage productDetailPage;
    protected SignupLoginPage signupPage;
    protected CheckoutPage checkoutPage;

    public Page getPage() {
        return page;
    }

    private boolean headless() {
        return ConfigReader.getBooleanProperty("headless", true);
    }

    private double slowMo() {
        return ConfigReader.getIntProperty("slowMo", 0);
    }

    private String configuredBrowserPath() {
        String configuredPath = ConfigReader.getProperty("browserExecutablePath");
        if (configuredPath != null && !configuredPath.isBlank()) {
            return configuredPath;
        }

        String fromEnv = System.getenv("BROWSER_EXECUTABLE_PATH");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        String fromProperty = System.getProperty("browserExecutablePath");
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty;
        }

        for (String candidate : DEFAULT_BROWSER_PATHS) {
            if (Files.exists(Path.of(candidate))) {
                return candidate;
            }
        }
        return null;
    }

    private Map<String, String> playwrightEnv(String browserPath) {
        Map<String, String> env = new LinkedHashMap<>();
        if (browserPath != null) {
            env.put("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1");
        }
        env.put("PLAYWRIGHT_BROWSERS_PATH",
                Path.of("target", "playwright-browsers").toAbsolutePath().toString());
        return env;
    }

    @BeforeClass
    public void setupBrowser() {
        String browserPath = configuredBrowserPath();
        playwright = Playwright.create(new Playwright.CreateOptions()
                .setEnv(playwrightEnv(browserPath)));
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless())
                .setSlowMo(slowMo());

        if (browserPath != null) {
            launchOptions.setExecutablePath(Path.of(browserPath));
        }

        browser = playwright.chromium().launch(launchOptions);
    }

    @BeforeMethod
    public void initContext() {
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();
        page.onDialog(dialog -> dialog.accept());

        homePage = new HomePage(page);
        signupLoginPage = new SignupLoginPage(page);
        accountPage = new AccountInformationPage(page);
        productsPage = new ProductsPage(page);
        cartPage = new CartPage(page);
        paymentPage = new PaymentPage(page);
        contactPage = new ContactUsPage(page);
        productDetailPage = new ProductDetailPage(page);
        signupPage = new SignupLoginPage(page);
        checkoutPage = new CheckoutPage(page);
    }

    @Step("Create a fresh logged-in user for test setup")
    protected String createLoggedInUser(String name, String password) {
        homePage.clickSignupLogin();
        String email = signupLoginPage.fillSignupDetailsWithRandomEmail(name);
        accountPage.fillAccountDetails(password, "10", "July", "2000");
        accountPage.fillAddressDetails(
                "Test",
                "User",
                "Test Corp",
                "123 Test Lane",
                "United States",
                "Texas",
                "Austin",
                "73301",
                "1234567890");
        accountPage.clickCreateAccount();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
        return email;
    }

    @AfterMethod
    public void stopTracingAndClose(ITestResult result) {
        if (context == null) {
            return;
        }

        String tracePath = "traces/" + result.getName() + "_" + System.currentTimeMillis() + ".zip";
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
        context.close();
    }

    @AfterClass
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
