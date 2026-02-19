package automation_exercise;

import automation_exercise.pages.*;
import com.microsoft.playwright.*;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.nio.file.Paths;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    // Khai báo sẵn các trang để lớp con dùng luôn
    protected HomePage homePage;
    protected SignupLoginPage signupLoginPage;
    protected AccountInformationPage accountPage;

    protected ProductsPage productsPage;
    protected CartPage cartPage;
    protected PaymentPage paymentPage;

    protected ContactUsPage contactPage;

    protected ProductDetailPage productDetailPage;

    @BeforeClass
    public void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(1000));
    }

    @BeforeMethod
    public void initContext() {
        context = browser.newContext();
        // Bật tracing để soi lỗi
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();

        // KHỞI TẠO CÁC PAGE OBJECTS TẠI ĐÂY
        homePage = new HomePage(page);
        signupLoginPage = new SignupLoginPage(page);
        accountPage = new AccountInformationPage(page);

        // THÊM MỚI Ở ĐÂY
        productsPage = new ProductsPage(page);
        cartPage = new CartPage(page);
        paymentPage = new PaymentPage(page);
        contactPage = new ContactUsPage(page);
        productDetailPage = new ProductDetailPage(page);

    }

    @AfterMethod
    public void stopTracingAndClose(ITestResult result) {
        // Lưu trace nếu test bị lỗi
        String tracePath = "traces/" + result.getName() + "_" + System.currentTimeMillis() + ".zip";
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
        context.close();
    }

    @AfterClass
    public void tearDown() {
        browser.close();
        playwright.close();
    }
}