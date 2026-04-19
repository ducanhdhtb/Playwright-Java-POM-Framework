package utils;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        if (!(result.getInstance() instanceof BaseTest baseTest)) {
            return;
        }

        Page page = baseTest.getPage();
        if (page == null) {
            return;
        }

        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(result.getName() + "_Failed_Screenshot", new ByteArrayInputStream(screenshot));
        } catch (Exception ignored) {
        }

        try {
            Allure.addAttachment("Failed_URL",
                    new ByteArrayInputStream(page.url().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ignored) {
        }

        try {
            Allure.addAttachment("Failed_Title",
                    new ByteArrayInputStream(page.title().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ignored) {
        }

        try {
            Allure.addAttachment("Page_Source",
                    new ByteArrayInputStream(page.content().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ignored) {
        }
    }
}
