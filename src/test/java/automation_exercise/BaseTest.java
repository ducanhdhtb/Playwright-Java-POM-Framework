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
import utils.CookieManager;
import utils.PageFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
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

    protected ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    protected ThreadLocal<Browser> browser = new ThreadLocal<>();
    protected ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    protected ThreadLocal<Page> page = new ThreadLocal<>();

    // API layer — available to all tests for setup/teardown without UI
    protected ThreadLocal<ApiClient> apiClient = new ThreadLocal<>();
    protected ThreadLocal<UserApiHelper> userApi = new ThreadLocal<>();

    protected ThreadLocal<HomePage> homePage = new ThreadLocal<>();
    protected ThreadLocal<SignupLoginPage> signupLoginPage = new ThreadLocal<>();
    protected ThreadLocal<AccountInformationPage> accountPage = new ThreadLocal<>();
    protected ThreadLocal<ProductsPage> productsPage = new ThreadLocal<>();
    protected ThreadLocal<CartPage> cartPage = new ThreadLocal<>();
    protected ThreadLocal<PaymentPage> paymentPage = new ThreadLocal<>();
    protected ThreadLocal<ContactUsPage> contactPage = new ThreadLocal<>();
    protected ThreadLocal<ProductDetailPage> productDetailPage = new ThreadLocal<>();
    protected ThreadLocal<SignupLoginPage> signupPage = new ThreadLocal<>();
    protected ThreadLocal<CheckoutPage> checkoutPage = new ThreadLocal<>();
    protected ThreadLocal<PageFactory> pageFactory = new ThreadLocal<>();

    public Page getPage() {
        return page.get();
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
        playwright.set(Playwright.create(new Playwright.CreateOptions()
                .setEnv(playwrightEnv(browserPath))));
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
            case "firefox" -> playwright.get().firefox();
            case "webkit" -> playwright.get().webkit();
            default -> playwright.get().chromium();
        };
        browser.set(browserType.launch(launchOptions));

        // API client is created once per class alongside the browser
        apiClient.set(new ApiClient(playwright.get()));
        userApi.set(new UserApiHelper(apiClient.get()));
    }

    @BeforeMethod(alwaysRun = true)
    public void initContext() {
        // When running with TestNG groups filtering, configuration methods can be skipped unless alwaysRun=true.
        // This guard keeps tests from NPE-ing in case setupBrowser wasn't invoked for any reason.
        if (browser.get() == null) {
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

        context.set(browser.get().newContext(contextOptions));

        TracingMode tracingMode = tracingMode();
        if (tracingMode != TracingMode.OFF) {
            context.get().tracing().start(new Tracing.StartOptions()
                    .setScreenshots(tracingScreenshots())
                    .setSnapshots(tracingSnapshots())
                    .setSources(tracingSources()));
        }

        page.set(context.get().newPage());
        page.get().setDefaultTimeout(defaultTimeoutMs());
        page.get().setDefaultNavigationTimeout(navigationTimeoutMs());
        PlaywrightAssertions.setDefaultAssertionTimeout(assertionTimeoutMs());
        page.get().onDialog(dialog -> dialog.accept());

        pageFactory.set(new PageFactory(page.get()));

        homePage.set(pageFactory.get().getHomePage());
        signupLoginPage.set(pageFactory.get().getSignupLoginPage());
        accountPage.set(pageFactory.get().getAccountInformationPage());
        productsPage.set(pageFactory.get().getProductsPage());
        cartPage.set(pageFactory.get().getCartPage());
        paymentPage.set(pageFactory.get().getPaymentPage());
        contactPage.set(pageFactory.get().getContactUsPage());
        productDetailPage.set(pageFactory.get().getProductDetailPage());
        signupPage.set(pageFactory.get().getSignupLoginPage());
        checkoutPage.set(pageFactory.get().getCheckoutPage());
    }

    @Step("Create a fresh logged-in user for test setup")
    protected String createLoggedInUser(String name, String password) {
        homePage.get().clickSignupLogin();
        String email = signupLoginPage.get().fillSignupDetailsWithRandomEmail(name);
        accountPage.get().fillAccountDetails(password, "10", "July", "2000");
        accountPage.get().fillAddressDetails(
                "Test",
                "User",
                "Test Corp",
                "123 Test Lane",
                "United States",
                "Texas",
                "Austin",
                "73301",
                "1234567890");
        accountPage.get().clickCreateAccount();
        page.get().getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
        return email;
    }

    // ── Cookie session helpers ────────────────────────────────────────────────

    /**
     * Save current browser session cookies under a given name.
     * Use after login to reuse the session in other tests.
     */
    @Step("Saving session as '{0}'")
    protected void saveSession(String sessionName) {
        CookieManager.save(context.get(), sessionName);
    }

    /**
     * Restore a previously saved cookie session into the current context.
     * Call before page.navigate() to skip the login flow.
     */
    @Step("Restoring session '{0}'")
    protected void restoreSession(String sessionName) {
        CookieManager.restore(context.get(), sessionName);
    }

    /**
     * Login via API-created user, save session cookies, return email.
     * Subsequent tests can call restoreSession() to skip login UI.
     */
    @Step("Login and save session as '{1}'")
    protected String loginAndSaveSession(String name, String password, String sessionName) {
        String email = userApi.get().setupUser(name, password);
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickSignupLogin();
        signupLoginPage.get().fillLoginForm(email, password);
        signupLoginPage.get().clickLoginButton();
        homePage.get().verifyLoggedInAs(name);
        saveSession(sessionName);
        return email;
    }

    @AfterMethod(alwaysRun = true)
    public void stopTracingAndClose(ITestResult result) {
        if (context.get() == null) {
            return;
        }

        String testName = (result != null && result.getName() != null && !result.getName().isBlank())
                ? result.getName()
                : "test";
        boolean passed = result != null && result.isSuccess();
        String className = (result != null && result.getTestClass() != null && result.getTestClass().getName() != null)
                ? result.getTestClass().getName()
                : "TestClass";

        Video video = null;
        try {
            if (page.get() != null) {
                video = page.get().video();
            }
        } catch (Exception ignored) {
        }

        // Persist a failure snapshot for Jenkins/email debugging.
        // Allure already gets attachments via listener/aspect, but Jenkins needs files to link as artifacts.
        if (!passed && page.get() != null) {
            try {
                Path dir = Paths.get("target", "artifacts", "failures");
                Files.createDirectories(dir);

                String base = (className + "_" + testName).replaceAll("[^A-Za-z0-9_.-]", "_");
                long ts = System.currentTimeMillis();
                Path screenshotPath = dir.resolve(base + "_" + ts + ".png");
                Path htmlPath = dir.resolve(base + "_" + ts + ".html");
                Path metaPath = dir.resolve(base + "_" + ts + ".txt");

                if (!page.get().isClosed()) {
                    page.get().screenshot(new Page.ScreenshotOptions()
                            .setFullPage(true)
                            .setPath(screenshotPath));
                }

                try {
                    String html = page.get().content();
                    Files.writeString(htmlPath, html, StandardCharsets.UTF_8);
                } catch (Exception ignored) {
                }

                try {
                    String meta = "class=" + className + "\n"
                            + "test=" + testName + "\n"
                            + "url=" + (page.get().isClosed() ? "" : page.get().url()) + "\n"
                            + "title=" + (page.get().isClosed() ? "" : page.get().title()) + "\n";
                    Files.writeString(metaPath, meta, StandardCharsets.UTF_8);
                } catch (Exception ignored) {
                }
            } catch (Exception ignored) {
            }
        }

        TracingMode tracingMode = tracingMode();
        if (tracingMode != TracingMode.OFF) {
            try {
                Files.createDirectories(tracingDir());
            } catch (Exception ignored) {
            }

            if (tracingMode == TracingMode.ON || (tracingMode == TracingMode.RETAIN_ON_FAILURE && !passed)) {
                Path tracePath = tracingDir().resolve(testName + "_" + System.currentTimeMillis() + ".zip");
                context.get().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
            } else {
                context.get().tracing().stop();
            }
        }

        // Video is recorded by context options; only decide whether to keep it.
        VideoMode videoMode = videoMode();

        context.get().close();

        if (videoMode == VideoMode.RETAIN_ON_FAILURE && passed && video != null) {
            try {
                video.delete();
            } catch (Exception ignored) {
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (browser.get() != null) {
            browser.get().close();
        }
        if (apiClient.get() != null) {
            apiClient.get().dispose();
        }
        if (playwright.get() != null) {
            playwright.get().close();
        }
    }
}
