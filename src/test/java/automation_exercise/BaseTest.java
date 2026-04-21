package automation_exercise;

import api.ApiClient;
import api.UserApiHelper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
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
    private enum TracingMode { OFF, ON, RETAIN_ON_FAILURE }
    private enum VideoMode { OFF, ON, RETAIN_ON_FAILURE }

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

    // API layer — available to all tests for setup/teardown without UI
    protected ApiClient apiClient;
    protected UserApiHelper userApi;

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
        return ConfigReader.getBooleanProperty(
                "playwright.headless",
                ConfigReader.getBooleanProperty("headless", true)
        );
    }

    private double slowMo() {
        return ConfigReader.getIntProperty(
                "playwright.slowMoMs",
                ConfigReader.getIntProperty("slowMo", 0)
        );
    }

    private int defaultTimeoutMs() {
        return ConfigReader.getIntProperty("playwright.defaultTimeoutMs", 30_000);
    }

    private int navigationTimeoutMs() {
        return ConfigReader.getIntProperty("playwright.navigationTimeoutMs", 30_000);
    }

    private int assertionTimeoutMs() {
        return ConfigReader.getIntProperty("playwright.assertionTimeoutMs", 5_000);
    }

    private String browserName() {
        return ConfigReader.getProperty("playwright.browser", "chromium").trim().toLowerCase();
    }

    private String browserChannel() {
        return ConfigReader.getProperty("playwright.channel");
    }

    private int viewportWidth() {
        return ConfigReader.getIntProperty("playwright.viewport.width", 0);
    }

    private int viewportHeight() {
        return ConfigReader.getIntProperty("playwright.viewport.height", 0);
    }

    private String locale() {
        return ConfigReader.getProperty("playwright.locale");
    }

    private String timezoneId() {
        return ConfigReader.getProperty("playwright.timezoneId");
    }

    private boolean ignoreHttpsErrors() {
        return ConfigReader.getBooleanProperty("playwright.ignoreHttpsErrors", false);
    }

    private static TracingMode parseTracingMode(String raw) {
        if (raw == null) {
            return TracingMode.RETAIN_ON_FAILURE;
        }
        return switch (raw.trim().toLowerCase()) {
            case "off", "false", "0" -> TracingMode.OFF;
            case "on", "true", "1" -> TracingMode.ON;
            case "retain-on-failure", "retain" -> TracingMode.RETAIN_ON_FAILURE;
            default -> TracingMode.RETAIN_ON_FAILURE;
        };
    }

    private static VideoMode parseVideoMode(String raw) {
        if (raw == null) {
            return VideoMode.RETAIN_ON_FAILURE;
        }
        return switch (raw.trim().toLowerCase()) {
            case "off", "false", "0" -> VideoMode.OFF;
            case "on", "true", "1" -> VideoMode.ON;
            case "retain-on-failure", "retain" -> VideoMode.RETAIN_ON_FAILURE;
            default -> VideoMode.RETAIN_ON_FAILURE;
        };
    }

    private TracingMode tracingMode() {
        return parseTracingMode(ConfigReader.getProperty("playwright.tracing", "retain-on-failure"));
    }

    private boolean tracingScreenshots() {
        return ConfigReader.getBooleanProperty("playwright.tracing.screenshots", true);
    }

    private boolean tracingSnapshots() {
        return ConfigReader.getBooleanProperty("playwright.tracing.snapshots", true);
    }

    private boolean tracingSources() {
        return ConfigReader.getBooleanProperty("playwright.tracing.sources", true);
    }

    private Path tracingDir() {
        return Paths.get(ConfigReader.getProperty("playwright.tracing.dir", "traces"));
    }

    private VideoMode videoMode() {
        return parseVideoMode(ConfigReader.getProperty("playwright.video", "retain-on-failure"));
    }

    private Path videoDir() {
        return Paths.get(ConfigReader.getProperty("playwright.video.dir", "target/videos"));
    }

    private int videoWidth() {
        return ConfigReader.getIntProperty("playwright.video.width", 0);
    }

    private int videoHeight() {
        return ConfigReader.getIntProperty("playwright.video.height", 0);
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

    @BeforeClass(alwaysRun = true)
    public void setupBrowser() {
        String browserPath = configuredBrowserPath();
        playwright = Playwright.create(new Playwright.CreateOptions()
                .setEnv(playwrightEnv(browserPath)));
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless())
                .setSlowMo(slowMo());

        String channel = browserChannel();
        if (channel != null && !channel.isBlank()) {
            launchOptions.setChannel(channel.trim());
        }

        if (browserPath != null) {
            launchOptions.setExecutablePath(Path.of(browserPath));
        }

        BrowserType browserType = switch (browserName()) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();
        };
        browser = browserType.launch(launchOptions);

        // API client is created once per class alongside the browser
        apiClient = new ApiClient(playwright);
        userApi = new UserApiHelper(apiClient);
    }

    @BeforeMethod(alwaysRun = true)
    public void initContext() {
        // When running with TestNG groups filtering, configuration methods can be skipped unless alwaysRun=true.
        // This guard keeps tests from NPE-ing in case setupBrowser wasn't invoked for any reason.
        if (browser == null) {
            setupBrowser();
        }

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(ignoreHttpsErrors());

        int vw = viewportWidth();
        int vh = viewportHeight();
        if (vw > 0 && vh > 0) {
            contextOptions.setViewportSize(vw, vh);
        }

        String locale = locale();
        if (locale != null && !locale.isBlank()) {
            contextOptions.setLocale(locale.trim());
        }

        String tz = timezoneId();
        if (tz != null && !tz.isBlank()) {
            contextOptions.setTimezoneId(tz.trim());
        }

        VideoMode videoMode = videoMode();
        if (videoMode != VideoMode.OFF) {
            contextOptions.setRecordVideoDir(videoDir());
            int videoW = videoWidth();
            int videoH = videoHeight();
            if (videoW > 0 && videoH > 0) {
                contextOptions.setRecordVideoSize(videoW, videoH);
            }
        }

        context = browser.newContext(contextOptions);

        TracingMode tracingMode = tracingMode();
        if (tracingMode != TracingMode.OFF) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(tracingScreenshots())
                    .setSnapshots(tracingSnapshots())
                    .setSources(tracingSources()));
        }

        page = context.newPage();
        page.setDefaultTimeout(defaultTimeoutMs());
        page.setDefaultNavigationTimeout(navigationTimeoutMs());
        PlaywrightAssertions.setDefaultAssertionTimeout(assertionTimeoutMs());
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

    @AfterMethod(alwaysRun = true)
    public void stopTracingAndClose(ITestResult result) {
        if (context == null) {
            return;
        }

        String testName = (result != null && result.getName() != null && !result.getName().isBlank())
                ? result.getName()
                : "test";
        boolean passed = result != null && result.isSuccess();

        Video video = null;
        try {
            if (page != null) {
                video = page.video();
            }
        } catch (Exception ignored) {
        }

        TracingMode tracingMode = tracingMode();
        if (tracingMode != TracingMode.OFF) {
            try {
                Files.createDirectories(tracingDir());
            } catch (Exception ignored) {
            }

            if (tracingMode == TracingMode.ON || (tracingMode == TracingMode.RETAIN_ON_FAILURE && !passed)) {
                Path tracePath = tracingDir().resolve(testName + "_" + System.currentTimeMillis() + ".zip");
                context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
            } else {
                context.tracing().stop();
            }
        }

        // Video is recorded by context options; only decide whether to keep it.
        VideoMode videoMode = videoMode();

        context.close();

        if (videoMode == VideoMode.RETAIN_ON_FAILURE && passed && video != null) {
            try {
                video.delete();
            } catch (Exception ignored) {
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (apiClient != null) {
            apiClient.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
