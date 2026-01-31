package utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;
import tests.BaseTest;
import java.io.ByteArrayInputStream;

public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        Object testClass = result.getInstance();
        // Lấy page từ BaseTest để chụp ảnh
        Page page = ((BaseTest) testClass).getPage();

        if (page != null) {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(result.getName() + "_Failed_Screenshot", new ByteArrayInputStream(screenshot));
        }
    }
}