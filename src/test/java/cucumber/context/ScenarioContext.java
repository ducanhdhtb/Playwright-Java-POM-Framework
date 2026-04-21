package cucumber.context;

import api.ApiClient;
import api.UserApiHelper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.WaitForSelectorState;
import pages.*;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Shared state container for a single Cucumber scenario.
 * Instantiated once per scenario via PicoContainer (cucumber-picocontainer)
 * or manually passed between step definition classes.
 *
 * Holds Playwright browser/page lifecycle + page objects + API helpers.
 */
public class ScenarioContext {

    // ── Playwright ────────────────────────────────────────────────────────────
    public Playwright playwright;
    public Browser browser;
    public BrowserContext browserContext;
    public Page page;

    // ── Page Objects ──────────────────────────────────────────────────────────
    public HomePage homePage;
    public SignupLoginPage signupLoginPage;
    public AccountInformationPage accountPage;
    public ProductsPage productsPage;
    public CartPage cartPage;
    public CheckoutPage checkoutPage;
    public PaymentPage paymentPage;
    public ContactUsPage contactPage;
    public ProductDetailPage productDetailPage;

    // ── API Layer ─────────────────────────────────────────────────────────────
    public ApiClient apiClient;
    public UserApiHelper userApi;

    // ── Scenario-level data (shared between step classes) ─────────────────────
    /** Email generated during API user setup — reused in login steps. */
    public String apiCreatedEmail;
    /** Password used when creating the API user. */
    public String apiCreatedPassword;

    // ─────────────────────────────────────────────────────────────────────────

    public void initBrowser() {
        playwright = Playwright.create();
        apiClient = new ApiClient(playwright);
        userApi = new UserApiHelper(apiClient);

        String browserName = ConfigReader.getProperty("playwright.browser", "chromium").trim().toLowerCase();
        boolean headless = ConfigReader.getBooleanProperty("playwright.headless", true);
        double slowMo = ConfigReader.getIntProperty("playwright.slowMoMs", 0);

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo);

        BrowserType browserType = switch (browserName) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();
        };

        browser = browserType.launch(launchOptions);

        Browser.NewContextOptions ctxOptions = new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true);

        browserContext = browser.newContext(ctxOptions);
        page = browserContext.newPage();

        int defaultTimeout = ConfigReader.getIntProperty("playwright.defaultTimeoutMs", 60_000);
        int navTimeout = ConfigReader.getIntProperty("playwright.navigationTimeoutMs", 90_000);
        int assertTimeout = ConfigReader.getIntProperty("playwright.assertionTimeoutMs", 10_000);

        page.setDefaultTimeout(defaultTimeout);
        page.setDefaultNavigationTimeout(navTimeout);
        PlaywrightAssertions.setDefaultAssertionTimeout(assertTimeout);
        page.onDialog(dialog -> dialog.accept());

        // Init page objects
        homePage = new HomePage(page);
        signupLoginPage = new SignupLoginPage(page);
        accountPage = new AccountInformationPage(page);
        productsPage = new ProductsPage(page);
        cartPage = new CartPage(page);
        checkoutPage = new CheckoutPage(page);
        paymentPage = new PaymentPage(page);
        contactPage = new ContactUsPage(page);
        productDetailPage = new ProductDetailPage(page);
    }

    public void tearDown() {
        if (browserContext != null) {
            try { browserContext.close(); } catch (Exception ignored) {}
        }
        if (browser != null) {
            try { browser.close(); } catch (Exception ignored) {}
        }
        if (apiClient != null) {
            apiClient.dispose();
        }
        if (playwright != null) {
            try { playwright.close(); } catch (Exception ignored) {}
        }
    }

    /** Attach screenshot to Allure on failure. */
    public byte[] takeScreenshot() {
        if (page != null) {
            try {
                return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            } catch (Exception ignored) {}
        }
        return null;
    }
}
