package tests;

import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import utils.ConfigReader;
import java.nio.file.Paths;

@Listeners(utils.TestListener.class)
public class BaseTest {
    private static final Logger log = LogManager.getLogger(BaseTest.class);
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    public Page getPage() { return page; }

    @BeforeSuite
    public void beforeSuite() {
        ConfigReader.init_prop();
    }

    @BeforeMethod
    public void setup() {
        log.info("Khởi tạo Playwright và Browser...");
        playwright = Playwright.create();

        boolean isHeadless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));
        double slowMo = Double.parseDouble(ConfigReader.getProperty("slowMo"));

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(isHeadless)
                .setSlowMo(slowMo));

        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("target/videos/")));
        page = context.newPage();
    }

    @AfterMethod
    public void tearDown() {
        if (context != null) context.close();
        if (browser != null) {
            browser.close();
            playwright.close();
        }
    }
}